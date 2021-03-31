package com.encodeering.luminous.application.internal.partner.quote

import com.encodeering.luminous.application.api.partner.quote.Quote
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock.fixed
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit.MINUTES

/**
 * @author clausen - encodeering@gmail.com
 */
internal class QuoteCandleCharterTest {

    @Test
    fun `chart should expand candles up till now` () {
        val         chart = QuoteCandleCharter (clock).chart (listOf (quoteT1, quoteT2), MINUTES)

        assertThat (chart.map { it.opened }).containsExactly (
            quoteT1.timestamp.truncatedTo (MINUTES),
            quoteT2.timestamp.truncatedTo (MINUTES),
            quoteT3.timestamp.truncatedTo (MINUTES),
            quoteT4.timestamp.truncatedTo (MINUTES),
        )
    }

    @Test
    fun `chart should expand candles in between` () {
        val         chart = QuoteCandleCharter (clock).chart (listOf (quoteT1, quoteT4), MINUTES)

        assertThat (chart.map { it.opened }).containsExactly (
            quoteT1.timestamp.truncatedTo (MINUTES),
            quoteT2.timestamp.truncatedTo (MINUTES),
            quoteT3.timestamp.truncatedTo (MINUTES),
            quoteT4.timestamp.truncatedTo (MINUTES),
        )
    }

    @Test
    fun `chart should align the opened and closed timestamp` () {
        val         chart = QuoteCandleCharter (clock).chart (listOf (quoteT3, quoteT4), MINUTES)

        assertThat (chart.map { it.opened }).containsExactly (
            quoteT3.timestamp.truncatedTo (MINUTES),
            quoteT4.timestamp.truncatedTo (MINUTES),
        )
        assertThat (chart.map { it.closed }).containsExactly (
            quoteT3.timestamp.plusMinutes (1).truncatedTo (MINUTES),
            quoteT4.timestamp.plusMinutes (1).truncatedTo (MINUTES),
        )
    }

    @Test
    fun `chart should return an empty list if no quotes are present` () {
        val         charter = QuoteCandleCharter (clock)

        assertThat (charter.chart (emptyList (), MINUTES)).isEmpty ()
    }

    @Test
    fun `chart should return only valid candles` () {
        val         charter = QuoteCandleCharter (clock)

        assertThat (charter.chart (listOf (quoteT4), MINUTES).map { it.open }).containsExactly (quoteT4.price)
    }

    internal companion object {

        private val timestamp = OffsetDateTime.now ()

        private val quoteT1 = Quote ("VE1506683Q53", "321.4324".toBigDecimal (), timestamp.plusMinutes (1))
        private val quoteT2 = Quote ("VE1506683Q53", "178.4324".toBigDecimal (), timestamp.plusMinutes (2))
        private val quoteT3 = Quote ("VE1506683Q53", "678.4324".toBigDecimal (), timestamp.plusMinutes (3))
        private val quoteT4 = Quote ("VE1506683Q53", "478.4324".toBigDecimal (), timestamp.plusMinutes (4))

        private val clock = fixed (timestamp.plusMinutes (4).toInstant (), UTC)

    }

}
