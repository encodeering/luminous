package com.encodeering.luminous.system.partner.internal.marketplace

import com.encodeering.luminous.system.partner.testing.ktor.Demos
import com.encodeering.luminous.system.partner.testing.ws.readText
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Clock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MarketplaceModuleTest {

    @ParameterizedTest
    @MethodSource ("messages")
    fun `should configure a websocket for quotes` (path: String, pattern: Regex) {
        val latch = CountDownLatch (1)

        demo {
            val wsc = createClient {
                install (WebSockets)
            }

            coroutineScope {
                launch (IO) {
                    wsc.webSocket (path) {
                        assertThat (incoming.readText ()).containsPattern (pattern.toPattern ())

                        close ()

                        for (            frame in incoming) {
                            assertThat ((frame as Frame.Text).readText ()).containsPattern (pattern.toPattern ())
                        }

                        latch.countDown ()
                    }
                }
            }
        }

        latch.await ()
    }

    private fun demo (f: suspend ApplicationTestBuilder.() -> Unit) = Demos.demo {
        application {
            MarketplaceModule (Clock.systemUTC (), this, rate = 100, unit = MILLISECONDS).load ()
        }

        f ()
    }

    private companion object {

        @JvmStatic
        fun messages () = listOf (
            Arguments.of ("/quotes",      """"type":"QUOTE"""".toRegex ()),
            Arguments.of ("/instruments", """"type":"(ADD|DELETE)"""".toRegex ())
        )

    }

}
