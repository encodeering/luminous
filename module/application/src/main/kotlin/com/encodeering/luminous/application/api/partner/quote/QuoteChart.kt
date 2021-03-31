package com.encodeering.luminous.application.api.partner.quote

import com.encodeering.luminous.application.api.trade.Candle
import java.time.temporal.ChronoUnit

/**
 * @author clausen - encodeering@gmail.com
 */
interface QuoteChart {

    fun chart (quotes: List<Quote>, unit: ChronoUnit): List<Candle>

}
