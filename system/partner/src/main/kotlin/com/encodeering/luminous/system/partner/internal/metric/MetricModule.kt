package com.encodeering.luminous.system.partner.internal.metric

import com.encodeering.luminous.system.partner.internal.ktor.Module
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MetricModule (private val application: Application): Module {

    private val prometheus = PrometheusMeterRegistry (PrometheusConfig.DEFAULT)

    override fun load () = with (application) {
        install (MicrometerMetrics) {
            registry = prometheus
        }

        routing {
            get ("/internal/prometheus") { call.respond (prometheus.scrape ()) }
        }

        return@with
    }

}
