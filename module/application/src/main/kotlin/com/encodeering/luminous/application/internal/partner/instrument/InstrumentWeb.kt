package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.quarkus.vertx.web.RoutingExchange
import io.vertx.core.json.Json
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
@RouteBase (path = "/instrument", produces = ["application/json"])
internal class InstrumentWeb (private val instruments: InstrumentRepository) {

    @Route (path = "/")
    fun instruments (): Set<Instrument> =
        instruments.all ()

    @Route (path = "/:isin")
    fun instrument (@Param ("isin") isin: String, exchange: RoutingExchange): Unit = when (val instrument = instruments.one (isin)) {
        null -> exchange.notFound ().end ()
        else -> exchange.ok (Json.encode (instrument))
    }

}
