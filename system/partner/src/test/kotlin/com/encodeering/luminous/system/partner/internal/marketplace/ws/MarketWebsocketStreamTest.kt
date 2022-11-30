package com.encodeering.luminous.system.partner.internal.marketplace.ws

import com.encodeering.luminous.system.partner.api.io.Messenger
import com.encodeering.luminous.system.partner.api.marketplace.Marketplace
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.CREATE
import com.encodeering.luminous.system.partner.internal.io.Errors.quietly
import com.encodeering.luminous.system.partner.internal.marketplace.Config
import com.encodeering.luminous.system.partner.internal.marketplace.MarketplaceDefault
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStream.Companion.asNotifier
import com.encodeering.luminous.system.partner.internal.ws.WebsocketStream
import com.encodeering.luminous.system.partner.testing.co.TestNumber
import com.encodeering.luminous.system.partner.testing.co.TestNumberDefault
import com.encodeering.luminous.system.partner.testing.io.RecordingMessenger
import com.encodeering.luminous.system.partner.testing.playground.Assets
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.lang.Thread.currentThread
import java.time.Clock
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MarketWebsocketStreamTest {

    @Test
    fun `subscribe should be called when the stream is used` () {
        val messenger = messenger (2)

        val marketplace = marketplace ()
            marketplace.stimulate ()
            marketplace.stimulate ()

        demo (marketplace, false, messenger) {
              marketplace.stimulate ()
              marketplace.stimulate ()
        }

        assertThat (messenger.recordings ()).hasSize (2)
    }

    @Test
    fun `subscribe should be called before dump` () {
        val   marketplace = marketplace ()
        demo (marketplace, true, messenger ()) { marketplace.stimulate () }

        inOrder    (marketplace) {
            verify (marketplace).subscribe (any ())
            verify (marketplace).dump (any ())
        }
    }

    @Test
    fun `dump should only be called if requested` () {
        val   marketplace = marketplace ()
        demo (marketplace, false, messenger ()) { marketplace.stimulate () }

        verify (marketplace).subscribe (any ())
        verify (marketplace, never ()).dump (any ())
    }

    @Test
    fun `dump should start in RECORD phase and swap from RECORD to REPLAY phase on completion` () {
        val messenger = messenger (4)

        val marketplace = marketplace ()
            marketplace.stimulate ()
            marketplace.stimulate ()

        demo (marketplace, true, messenger) {
              marketplace.stimulate ()
              marketplace.stimulate ()
        }

        assertThat (messenger.recordings ()).hasSize (4)
    }

    @Test
    fun `dump and subscribe should run on a dispatcher` () {
        val names = ConcurrentLinkedQueue<String> ()

        val messenger = messenger (2)

        val marketplace = marketplace {
            doAnswer { names += currentThread ().name; it.callRealMethod () }.whenever (this).subscribe (any ())
            doAnswer { names += currentThread ().name; it.callRealMethod () }.whenever (this).dump      (any ())
        }

              marketplace.stimulate ()
        demo (marketplace, true, messenger) {
            assertThat (currentThread ().name).doesNotStartWith   ("DefaultDispatcher-worker-")
            assertThat (names.toList ()).allMatch { it.startsWith ("DefaultDispatcher-worker-") }

            marketplace.stimulate ()
        }

        assertThat (messenger.recordings ()).hasSize (2)
    }

    @Test
    fun `use should not be nested` () {
        val messenger = messenger ()

        val   marketplace = marketplace ()
        demo (marketplace, false, messenger) {
            assertThrows<IllegalStateException> {
                use {  }
            }

            marketplace.stimulate ()
        }

        assertThat (messenger.recordings ()).hasSize (1)
    }

    @Test
    fun `use should call f with the outer context` () {
        val messenger = messenger ()

        val   marketplace = marketplace ()
        demo (marketplace, false, messenger, TestNumberDefault (42)) {
            coroutineScope {
                assertThat (currentThread ().name).startsWith ("pool")
                assertThat (coroutineContext[TestNumber]!!.number).isEqualTo (42)
            }

            marketplace.stimulate ()
        }

        assertThat (messenger.recordings ()).hasSize (1)
    }

    @Test
    fun `close should unsubscribe on runtime errors` () {
        val subscriptions = ConcurrentLinkedQueue<Runnable> ()

        val marketplace = marketplace {
            doAnswer { subscriptions += subscription ().apply (Runnable::run); it.callRealMethod () }.whenever (this).subscribe (any ())
            doThrow                                                      ( IllegalStateException () ).whenever (this).dump      (any ())
        }

        assertThrows<IllegalStateException> {
            demo (marketplace, true, messenger ()) {}
        }

        subscriptions.forEach { verify (it).run () }
    }

    @Test
    fun `close should unsubscribe at the end` () {
        val subscriptions = ConcurrentLinkedQueue<Runnable> ()

        val marketplace = marketplace {
            doAnswer { subscriptions += subscription ().apply (Runnable::run); it.callRealMethod () }.whenever (this).subscribe (any ())
        }

        demo (marketplace, false, messenger ()) {
              marketplace.stimulate ()
        }

        subscriptions.forEach { verify (it).run () }
    }

    @Test
    fun `as notifier simply provides an adapter` () {
        val     messenger = mock<Messenger<MarketMessage>> ()
                messenger.asNotifier ().notify (CREATE, asset)
        verify (messenger).send (MarketMessage (CREATE, asset))
    }

    @OptIn (DelicateCoroutinesApi::class)
    private fun demo (marketplace: MarketplaceDefault, dump: Boolean, messenger: RecordingMessenger<MarketMessage>, context: CoroutineContext = EmptyCoroutineContext, f: suspend WebsocketStream<MarketMessage>.() -> Unit) = runBlocking {
        val outer = currentThread ()

        val await = Channel<Unit> ()

        val     pool = newFixedThreadPoolContext (2, "pool")
        launch (pool + context) {
            stream (marketplace, dump, messenger).use {
                f ()
                await.receive ()
            }
        }.invokeOnCompletion {
            quietly (pool::close)

            if (it != null)
                outer.interrupt ()
        }

        messenger.await () // a blocking call might be okay for a testing purpose, but otherwise not as great in conjunction with co

        await.send (Unit)
    }

    private companion object {

        val asset = Assets.asset ()

        fun subscription () = mock<Runnable> ()

        fun messenger (counter: Int = 1) = RecordingMessenger<MarketMessage> (counter) { it.code == CREATE }

        fun marketplace (configure: Marketplace.() -> Unit = {}) = spy (MarketplaceDefault (Clock.systemUTC (), Config (instruments = Config.Instrument (100, 1.0, 0.0)))).apply (configure)

        fun stream (marketplace: Marketplace, dump: Boolean, messenger: Messenger<MarketMessage>) = MarketWebsocketStream (marketplace, dump, messenger)

    }

}
