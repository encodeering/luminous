package com.encodeering.luminous.system.partner.internal.marketplace

import com.encodeering.luminous.system.partner.api.marketplace.Marketplace
import com.encodeering.luminous.system.partner.internal.io.FixedRateScheduler
import com.encodeering.luminous.system.partner.internal.ktor.Module
import com.encodeering.luminous.system.partner.internal.ktor.decommission
import com.encodeering.luminous.system.partner.internal.ktor.events
import com.encodeering.luminous.system.partner.internal.ktor.started
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory.Companion.Key.INSTRUMENT
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory.Companion.Key.QUOTE
import com.encodeering.luminous.system.partner.internal.ws.WebsocketConnection
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import java.time.Clock
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MarketplaceModule (
    private val clock: Clock,
    private val application: Application,
    private val rate: Long = 1,
    private val unit: TimeUnit = SECONDS,
    private val ff: Int = 1000,
    private val factory: () -> Marketplace = { MarketplaceDefault (clock, Config ()) },
): Module {

    val marketplace: Marketplace by lazy { factory ().apply { repeat (ff) { stimulate () } } }

    override fun load () = with (application) {
        install (WebSockets)

        val streaming = MarketWebsocketStreamFactory (marketplace)

        val instruments = WebsocketConnection (INSTRUMENT, streaming)
        val quotes      = WebsocketConnection (QUOTE,      streaming)

        routing {
            webSocket ("/instruments") { with (instruments) { handle () } }
            webSocket ("/quotes")      { with (quotes)      { handle () } }
        }

        events {
            val           scheduler = FixedRateScheduler (marketplace::stimulate, rate, unit)
            started      (scheduler::start)
            decommission (scheduler::stop)
        }
    }

}
