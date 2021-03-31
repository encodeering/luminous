package com.encodeering.luminous.application.internal.partner.quote

import com.encodeering.luminous.application.api.io.reduce
import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.partner.quote.QuoteChart
import com.encodeering.luminous.application.api.trade.Candle
import java.math.BigDecimal
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
internal class QuoteCandleCharter (private val clock: Clock): QuoteChart {

    override fun chart (quotes: List<Quote>, unit: ChronoUnit): List<Candle> {
        val candles = quotes.groupBy { it.timestamp.truncatedTo (unit) }.mapValues {
            (_, v) -> v.candlefy ().realign {
                opened,                      closed ->
                opened.truncatedTo (unit) to closed.truncatedTo (unit).plus (1, unit)
            }
        }.values + marknow (unit)

        return candles.reduce (n = 1) {
            list,        next ->
            list.expand (next, unit)
            list += next
            list
        }.dropLastWhile { it is Marker }
    }

    private fun MutableList<Candle>.expand (next: Candle, unit: ChronoUnit): MutableList<Candle> {
        var tail = last ()
        if (tail is Marker)
            return this

        while (true) {
            if (tail.closed.isEqual (next.opened) || tail.closed.isAfter (next.opened))
                break

            tail = tail.realign {
                opened,                  closed ->
                opened.plus (1, unit) to closed.plus (1, unit)
            }

            this += tail
        }

        return this
    }

    private fun marknow (unit: ChronoUnit) = Marker (clock.instant ().atOffset (UTC).truncatedTo (unit).plus (1, unit))

    private class Marker (override val opened: OffsetDateTime): Candle {
        override val open: BigDecimal get ()       = throw UnsupportedOperationException ()
        override val close: BigDecimal get ()      = throw UnsupportedOperationException ()
        override val closed: OffsetDateTime get () = throw UnsupportedOperationException ()
        override val low: BigDecimal get ()        = throw UnsupportedOperationException ()
        override val high: BigDecimal get ()       = throw UnsupportedOperationException ()

        override fun realign (timing: (OffsetDateTime, OffsetDateTime) -> Pair<OffsetDateTime, OffsetDateTime>): Candle = throw UnsupportedOperationException ()
    }

}
