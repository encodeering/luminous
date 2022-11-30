package com.encodeering.luminous.system.partner.internal.ws

import com.encodeering.luminous.system.partner.api.io.Messenger
import com.encodeering.luminous.system.partner.testing.ws.readText
import com.encodeering.luminous.system.partner.testing.ws.stream
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author clausen - encodeering@gmail.com
 */
internal class WebsocketConnectionTest {

    @Test
    fun `ws should initiate the stream` () {
        val checks = AtomicInteger (8)

        val closed = CountDownLatch (1)

        connection (streaming (closed) { world.chunkedSequence (1) }).stream ("/hello", clients = 4) {
            clients ->
            clients.forEach {
                it.webSocket ("/hello") {
                    world.chunkedSequence (1).forEach {
                        assertThat (incoming.readText ()).isEqualTo (it)
                    }
                }

                checks.decrementAndGet ()
            }

            withContext (IO) {
                clients.map {
                    async {
                        it.webSocket ("/hello") {
                            world.chunkedSequence (1).forEach {
                                assertThat (incoming.readText ()).isEqualTo (it)
                            }
                        }

                        checks.decrementAndGet ()
                    }
                }.awaitAll ()
            }
        }

        closed.await ()

        assertThat (checks.get ()).isZero ()
    }

    @Test
    fun `ws should abort if a client sends a message (fail fast)` () {
        val checks = AtomicInteger (1)

        val closed = CountDownLatch (1)

        connection (streaming (closed) { emptySequence () }).stream ("/hello", clients = 1) { (client) ->
            assertThrows<ClosedReceiveChannelException> {
                client.webSocket ("/hello") {
                    outgoing.send (Frame.Text (""))

                    incoming.readText ()
                }
            }

            checks.decrementAndGet ()
        }

        closed.await ()

        assertThat (checks.get ()).isZero ()
    }

    @Test
    fun `ws should close if initiate fails` () {
        val checks = AtomicInteger (1)

        val closed = CountDownLatch (1)

        connection (streaming (closed) { generateSequence { throw IllegalStateException () } }).stream ("/hello", clients = 1) { (client) ->
            assertThrows<ClosedReceiveChannelException> {
                client.webSocket ("/hello") {
                    incoming.readText ()
                }
            }

            checks.decrementAndGet ()
        }

        closed.await ()

        assertThat (checks.get ()).isZero ()
    }

    private companion object {

        val world = "world"

        fun connection (streaming: WebsocketStreamFactory<Int, String>) = WebsocketConnection (1, streaming)

        fun streaming (closed: CountDownLatch, initials: () -> Sequence<String>) = object: WebsocketStreamFactory<Int, String> {

            override fun create (key: Int, messenger: Messenger<String>): WebsocketStream<String> =
                object: WebsocketStream<String> {

                    override val stream: Messenger<String> = messenger

                    override suspend fun use (f: suspend WebsocketStream<String>.() -> Unit) {
                        try {
                            withContext (IO) {
                                initials ().forEach (stream::send)
                            }

                            f ()
                        } finally {
                            closed.countDown ()
                        }
                    }
                }

        }

    }

}
