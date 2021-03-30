package com.encodeering.luminous.application.api.partner.quote

import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * @author clausen - encodeering@gmail.com
 */
data class Quote (val isin: String, val price: BigDecimal, val timestamp: OffsetDateTime)

interface QuoteRepository {

    fun remember (isin: String)

    fun forget (isin: String)

    fun all (isin: String): List<Quote>

    fun last (isin: String): Quote?

    fun add (quote: Quote): Quote

}
