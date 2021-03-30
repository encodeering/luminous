package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.internal.partner.quote.QuoteRepositoryMemory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import javax.annotation.Priority

/**
 * @author clausen - encodeering@gmail.com
 */
internal class InstrumentRepositoryQuoteBridgeTest {

    @Test
    fun `add will remember the isin for further quoting` () {
        val instruments = InstrumentRepositoryMemory ()
        val quotes = QuoteRepositoryMemory ()

        val         bridge = InstrumentRepositoryQuoteBridge (instruments, quotes)

                    quotes.add (quote)
        assertThat (quotes.all (instrument.isin)).isEmpty ()

                    bridge.add (instrument)

        assertThat (quotes.all (instrument.isin)).isEmpty ()
                    quotes.add (quote)
        assertThat (quotes.all (instrument.isin)).containsExactly (quote)
    }

    @Test
    fun `remove will forger the isin for further quoting` () {
        val instruments = InstrumentRepositoryMemory ()
        val quotes = QuoteRepositoryMemory ()

        val         bridge = InstrumentRepositoryQuoteBridge (instruments, quotes)

                    bridge.add    (instrument)
                    quotes.add    (quote)
        assertThat (quotes.all    (instrument.isin)).containsExactly (quote)

                    bridge.remove (instrument)

        assertThat (quotes.all    (instrument.isin)).isEmpty ()
                    quotes.add    (quote)
        assertThat (quotes.all    (instrument.isin)).isEmpty ()
    }

    @Test
    fun `priority should be one` () {
        assertThat (InstrumentRepositoryQuoteBridge::class.java.getAnnotation (Priority::class.java).value).isEqualTo (1)
    }

    internal companion object {

        private val instrument = Instrument ("VE1506683Q53", "hello")

        private val quote = Quote (instrument.isin, "678.4324".toBigDecimal (), OffsetDateTime.now ())

    }

}
