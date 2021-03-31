package com.encodeering.luminous.application.internal.partner.quote

import com.encodeering.luminous.application.api.partner.quote.Quote
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.OffsetDateTime

/**
 * @author clausen - encodeering@gmail.com
 */
internal class QuoteCandleTest {

    @Test
    fun `candlefy should throw an error if the list is empty` () {
        assertThrows<NoSuchElementException> {
            emptyList<Quote> ().candlefy ()
        }
    }

    @Test
    fun `candlefy should return a proper candle based on the input` () {
        assertThat (listOf (quoteT1, quoteT2, quoteT3, quoteT4).candlefy ()).isEqualTo (QuoteCandle (
            open  = "321.4324".toBigDecimal (),
            close = "478.4324".toBigDecimal (),

            low   = "178.4324".toBigDecimal (),
            high  = "678.4324".toBigDecimal (),

            opened = timestamp.plusMinutes (1),
            closed = timestamp.plusMinutes (4)
        ))
    }

    internal companion object {

        private val timestamp = OffsetDateTime.now ()

        private val quoteT1 = Quote ("VE1506683Q53", "321.4324".toBigDecimal (), timestamp.plusMinutes (1))
        private val quoteT2 = Quote ("VE1506683Q53", "178.4324".toBigDecimal (), timestamp.plusMinutes (2))
        private val quoteT3 = Quote ("VE1506683Q53", "678.4324".toBigDecimal (), timestamp.plusMinutes (3))
        private val quoteT4 = Quote ("VE1506683Q53", "478.4324".toBigDecimal (), timestamp.plusMinutes (4))

    }

}
