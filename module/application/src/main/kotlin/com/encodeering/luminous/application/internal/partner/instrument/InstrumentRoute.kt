package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.PartnerConfig
import org.apache.camel.builder.RouteBuilder
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
internal class InstrumentRoute (private val config: PartnerConfig): RouteBuilder () {

    override fun configure () {
        from ("ahc-ws://${config.endpoint}/instruments").routeId ("partner-instruments").to ("log:instruments")
    }

}
