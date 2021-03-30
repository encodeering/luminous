package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.partner.quote.QuoteRepository
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.quarkus.vertx.web.RoutingExchange
import io.vertx.core.json.Json
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
@RouteBase (path = "/instrument", produces = ["application/json"])
internal class InstrumentWeb (private val instruments: InstrumentRepository, private val quotes: QuoteRepository) {

    @Route (path = "/")
    fun instruments (): List<InstrumentEntity> =
        instruments.all ().map { InstrumentEntity (it, quotes.last (it.isin)) }

    @Route (path = "/:isin")
    fun instrument (@Param ("isin") isin: String, exchange: RoutingExchange): Unit = when (val instrument = instruments.one (isin)) {
        null -> exchange.notFound ().end ()
        else -> exchange.ok (Json.encode (InstrumentEntity (instrument, quotes.last (instrument.isin))))
    }

}

internal data class InstrumentEntity (val isin: String, val description: String, val price: Map<String, Price>?) {

    constructor (instrument: Instrument, quote: Quote?): this (
        instrument.isin,
        instrument.description,
        quote?.run {
            mapOf ("last" to Price (price, timestamp))
        }
    )

    data class Price (val price: BigDecimal, val timestamp: OffsetDateTime)

}
