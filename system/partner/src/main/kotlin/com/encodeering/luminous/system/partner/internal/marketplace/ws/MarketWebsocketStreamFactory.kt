package com.encodeering.luminous.system.partner.internal.marketplace.ws

import com.encodeering.luminous.system.partner.api.io.Messenger
import com.encodeering.luminous.system.partner.api.io.closeIf
import com.encodeering.luminous.system.partner.api.marketplace.Marketplace
import com.encodeering.luminous.system.partner.api.marketplace.Notifier
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase.CLOSED
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase.DIRECT
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase.RECORD
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase.REPLAY
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory.Companion.Key
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory.Companion.Key.INSTRUMENT
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory.Companion.Key.QUOTE
import com.encodeering.luminous.system.partner.internal.ws.WebsocketStream
import com.encodeering.luminous.system.partner.internal.ws.WebsocketStreamFactory
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.Closeable
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

internal class MarketWebsocketStreamFactory (private val marketplace: Marketplace): WebsocketStreamFactory<Key, MarketMessage> {

    private val bridge = MarketWebsocketMessengerBridge (Json)

    override fun create (key: Key, messenger: Messenger<String>): WebsocketStream<MarketMessage> = when (key) {
        INSTRUMENT -> bridge.instrument (messenger)
        QUOTE      -> bridge.quote      (messenger)
    }.let {
        MarketWebsocketStream (marketplace, key == INSTRUMENT, it)
    }

    internal companion object {

        enum class Key {
            INSTRUMENT,
            QUOTE
        }

    }

}

internal class MarketWebsocketStream (private val marketplace: Marketplace, val dump: Boolean, messenger: Messenger<MarketMessage>): WebsocketStream<MarketMessage> {

    private val phase = AtomicReference (CLOSED)

    override val stream = Delayer (phase, messenger)

    override suspend fun use (f: suspend WebsocketStream<MarketMessage>.() -> Unit) = withContext (IO) { // executes on a different thread only, but performs all open steps before f is called; required to avoid a potential co lock situation
        open ()
    }.use {
        f (this)
    }

    // a subscription needs to happen before a dump is performed, as messages might be missed otherwise, but they have to be delayed and passed through a deduplication later
    private fun open (): Closeable {
        check (phase.compareAndSet (CLOSED, if (dump) RECORD else DIRECT))

        val subscription = marketplace.subscribe (stream.asNotifier ())

        val closer = Closeable {
            phase.set (CLOSED)
            subscription.run ()
        }

        closer.closeIf {
            if (            dump)
                marketplace.dump (stream.asRecorder ().asNotifier ())

            phase.compareAndSet (RECORD, REPLAY)
        }

        return closer
    }

    internal companion object {

        fun Messenger<MarketMessage>.asNotifier () = Notifier { code, asset -> send (MarketMessage (code, asset)) }

    }

}

internal class Delayer (private val phase: AtomicReference<Phase>, private val delegatee: Messenger<MarketMessage>): Messenger<MarketMessage> {

    private val delayables: Queue<MarketMessage> = ConcurrentLinkedQueue ()
    private val recordings: Queue<MarketMessage> = ConcurrentLinkedQueue ()

    override fun send            (content: MarketMessage) = when (val p = phase.get ()) {
        DIRECT -> delegatee.send (content)
        RECORD -> delayables +=   content
        REPLAY -> {
            delayables += content
            delayables.removeAll (recordings.toSet ())
            delayables.forEach (delegatee::send)

            recordings.clear ()
            delayables.clear ()

            phase.compareAndSet (p, DIRECT)

            Unit
        }
        CLOSED -> throw UnsupportedOperationException ()
    }

    fun asRecorder () = Messenger<MarketMessage> {
        check (phase.get () == RECORD)

        recordings   += it
        delegatee.send (it)
    }

    internal enum class Phase { CLOSED, RECORD, REPLAY, DIRECT }

}
