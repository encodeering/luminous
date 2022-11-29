package com.encodeering.luminous.system.partner.internal.ws

import com.encodeering.luminous.system.partner.api.io.HandlerCo
import com.encodeering.luminous.system.partner.api.io.MessengerCo
import com.encodeering.luminous.system.partner.api.io.asMessenger
import io.ktor.server.request.host
import io.ktor.server.request.port
import io.ktor.server.request.uri
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.CloseReason.Codes.CANNOT_ACCEPT
import io.ktor.websocket.close
import io.ktor.websocket.send
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author clausen - encodeering@gmail.com
 */
internal class WebsocketConnection<K, T> (
    private val key: K,
    private val streaming: WebsocketStreamFactory<K, T>
): HandlerCo<WebSocketServerSession> {

    override suspend fun WebSocketServerSession.handle () {
        val info = info ()

        logger.info ("Serving a new connection [$info]")

        streaming.create (key, asMessenger ()).use {
            try {
                for (frame in incoming) {
                    close (CloseReason (CANNOT_ACCEPT, "unsupported")) // other options could be a hard-beat or c2 (re-dump, ..)
                }
            } catch          (e: RuntimeException) {
                logger.error (e.message, e)
            } finally {
                logger.info ("Ending a connection [$info]")
            }
        }
    }

    internal companion object {

        val logger: Logger = LoggerFactory.getLogger (WebsocketConnection::class.java)

        fun WebSocketServerSession.info () = "${call.request.uri}@${call.request.host ()}:${call.request.port ()}"

        fun WebSocketServerSession.asMessenger () = MessengerCo<String> (this::send).asMessenger (scope = this) // send should go through the correct scope

    }

}
