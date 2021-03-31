package com.encodeering.luminous.application.internal.partner.quote

import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.trade.Candle
import java.math.BigDecimal
import java.time.OffsetDateTime

internal data class QuoteCandle (override val open: BigDecimal, override val close: BigDecimal, override val low: BigDecimal, override val high: BigDecimal, override val opened: OffsetDateTime, override val closed: OffsetDateTime): Candle {

    override fun realign (timing: (OffsetDateTime, OffsetDateTime) -> Pair<OffsetDateTime, OffsetDateTime>): Candle {
        val         (         opened,          closed) = timing (opened, closed)
        return copy (opened = opened, closed = closed)
    }

}

internal fun Collection<Quote>.candlefy (): Candle {
    val first = first ()
    val last  = last ()

    var min = first.price
    var max = first.price

    forEach {
        min = minOf (min, it.price)
        max = maxOf (max, it.price)
    }

    return QuoteCandle (first.price, last.price, min, max, first.timestamp, last.timestamp)
}
