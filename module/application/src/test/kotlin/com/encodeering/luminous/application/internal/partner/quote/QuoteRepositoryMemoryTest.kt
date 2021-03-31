package com.encodeering.luminous.application.internal.partner.quote

import com.encodeering.luminous.application.api.partner.quote.Quote
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.OffsetDateTime

/**
 * @author clausen - encodeering@gmail.com
 */
internal class QuoteRepositoryMemoryTest {

    @Test
    fun `remember should create a quote space for a isin` () {
        val         quotes = QuoteRepositoryMemory (clock)

                    quotes.remember (quoteT1.isin)
                    quotes.add      (quoteT1)
        assertThat (quotes.all      (quoteT1.isin)).isNotEmpty ()
                    quotes.remember (quoteT1.isin)
        assertThat (quotes.all      (quoteT1.isin)).containsExactly (quoteT1)
    }

    @Test
    fun `forget should remove a quote space for a isin` () {
        val         quotes = QuoteRepositoryMemory (clock)

                    quotes.remember (quoteT1.isin)
                    quotes.add      (quoteT1)
        assertThat (quotes.all      (quoteT1.isin)).isNotEmpty ()
                    quotes.forget   (quoteT1.isin)
        assertThat (quotes.all      (quoteT1.isin)).isEmpty ()
    }

    @Test
    fun `add should add a quote to a space for a known isin` () {
        val         quotes = QuoteRepositoryMemory (clock)

                    quotes.remember (quoteT1.isin)
                    quotes.add      (quoteT1)
                    quotes.add      (quoteT1)
                    quotes.add      (quoteT2)
        assertThat (quotes.all      (quoteT1.isin)).containsExactly (quoteT1, quoteT2)
    }

    @Test
    fun `add should not a quote to a space otherwise` () {
        val         quotes = QuoteRepositoryMemory (clock)

                    quotes.add      (quoteT1)
        assertThat (quotes.all      (quoteT1.isin)).isEmpty ()
    }

    @Test
    fun `all should return all quotes from a known isin` () {
        val         quotes = QuoteRepositoryMemory (clock)

        assertThat (quotes.all      (quoteT1.isin)).isEmpty ()
                    quotes.remember (quoteT1.isin)
                    quotes.add      (quoteT1)
                    quotes.add      (quoteT2)
        assertThat (quotes.all      (quoteT1.isin)).containsExactly (quoteT1, quoteT2)
    }

    @Test
    fun `all should return an empty list otherwise` () {
        val         quotes = QuoteRepositoryMemory (clock)

        assertThat (quotes.all      (quoteT1.isin)).isEmpty ()
    }

    @Test
    fun `last should return the last quote from a known isin` () {
        val         quotes = QuoteRepositoryMemory (clock)

        assertThat (quotes.last     (quoteT1.isin)).isNull ()
                    quotes.remember (quoteT1.isin)
        assertThat (quotes.last     (quoteT1.isin)).isNull ()
                    quotes.add      (quoteT1)
        assertThat (quotes.last     (quoteT1.isin)).isEqualTo (quoteT1)
                    quotes.add      (quoteT2)
        assertThat (quotes.last     (quoteT1.isin)).isEqualTo (quoteT2)
    }

    @Test
    fun `last should return nothing otherwise` () {
        val         quotes = QuoteRepositoryMemory (clock)

        assertThat (quotes.last     (quoteT1.isin)).isNull ()
    }

    internal companion object {

        private val timestamp = OffsetDateTime.now ()

        private val quoteT1 = Quote ("VE1506683Q53", "321.4324".toBigDecimal (), timestamp.plusMinutes (1))
        private val quoteT2 = Quote ("VE1506683Q53", "678.4324".toBigDecimal (), timestamp.plusMinutes (2))

        private val clock = Clock.fixed (timestamp.plusMinutes (3).toInstant (), timestamp.offset)

    }

}
