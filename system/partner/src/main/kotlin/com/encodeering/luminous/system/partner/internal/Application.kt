package com.encodeering.luminous.system.partner.internal

import com.encodeering.luminous.system.partner.internal.ktor.Module
import com.encodeering.luminous.system.partner.internal.marketplace.MarketplaceModule
import com.encodeering.luminous.system.partner.internal.metric.MetricModule
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import java.time.Clock
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * @author clausen - encodeering@gmail.com
 */
internal fun main (args: Array<String>) = EngineMain.main (args)

internal fun Application.module () = ApplicationModule (this).load ()

internal class ApplicationModule (application: Application): Module {

    val clock: Clock = Clock.systemUTC ()

    val metric = MetricModule (application)
    val marketplace = MarketplaceModule (clock, application, rate = 100, unit = MILLISECONDS)

    private fun all () = listOf (
        metric,
        marketplace
    )

    override fun load () {
        all ().forEach { it.load () }
    }

}
