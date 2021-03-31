package com.encodeering.luminous.application.internal.partner.quote

import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.partner.quote.QuoteChart
import com.encodeering.luminous.application.api.partner.quote.QuoteRepository
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.quarkus.vertx.web.RoutingExchange
import io.vertx.core.json.Json
import java.time.temporal.ChronoUnit.MINUTES
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
@RouteBase (path = "/quote", produces = ["application/json"])
internal class QuoteWeb (private val quotes: QuoteRepository, private val charts: QuoteChart) {

    @Route (path = "/:isin/candle/1m")
    fun candles (@Param ("isin") isin: String, exchange: RoutingExchange): Unit = when (val list = quotes.all (isin)) {
        emptyList<Quote> () -> exchange.notFound ().end ()
        else                -> exchange.ok (Json.encode (charts.chart (list, MINUTES)))
    }

}
