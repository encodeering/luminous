package com.encodeering.luminous.system.partner.testing.ws

import com.encodeering.luminous.system.partner.internal.ws.WebsocketConnection
import com.encodeering.luminous.system.partner.testing.ktor.Demos
import io.ktor.client.HttpClient
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ReceiveChannel
import io.ktor.client.plugins.websocket.WebSockets as WebsocketsClient

/**
 * @author clausen - encodeering@gmail.com
 */
internal suspend fun ReceiveChannel<Frame>.readText () = (receive () as Frame.Text?)?.readText ()

internal fun <K, T> WebsocketConnection<K, T>.stream (path: String, clients: Int = 1, f: suspend WebsocketConnection<K, T>.(List<HttpClient>) -> Unit) {
    Demos.demo {
        install (WebSockets)

        routing {
            webSocket (path) { with (this@stream) { handle () } }
        }

        f ((0 until clients).map { createClient { install (WebsocketsClient) } })
    }
}
