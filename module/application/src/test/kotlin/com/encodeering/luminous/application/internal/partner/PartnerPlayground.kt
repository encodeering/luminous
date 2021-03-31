package com.encodeering.luminous.application.internal.partner

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.partner.quote.QuoteChart
import com.encodeering.luminous.application.api.partner.quote.QuoteRepository
import com.encodeering.luminous.application.internal.partner.instrument.InstrumentRepositoryMemory
import com.encodeering.luminous.application.internal.partner.quote.QuoteCandleCharter
import com.encodeering.luminous.application.internal.partner.quote.QuoteRepositoryMemory
import io.quarkus.arc.AlternativePriority
import java.time.Clock
import java.time.OffsetDateTime
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
internal class PartnerPlayground {

    private val timestamp = OffsetDateTime.parse ("2021-03-30T13:46:19.00+02:00")

    @Produces
    @AlternativePriority (2)
    fun clock (): Clock = Clock.fixed (timestamp.plusMinutes (2).toInstant (), timestamp.offset)

    @Produces
    @AlternativePriority (2)
    fun instruments (): InstrumentRepository = InstrumentRepositoryMemory ().apply {
        add (Instrument ("VE1506683Q53", "hello"))
        add (Instrument ("LF681P504335", "world"))
    }

    @Produces
    @AlternativePriority (2)
    fun quotes (clock: Clock): QuoteRepository = QuoteRepositoryMemory (clock).apply {
        remember   ("VE1506683Q53")

        add (Quote ("VE1506683Q53", "234.345".toBigDecimal (), timestamp))
        add (Quote ("VE1506683Q53", "345.456".toBigDecimal (), timestamp.plusMinutes (1)))
    }

    @Produces
    @AlternativePriority (2)
    fun charts (clock: Clock): QuoteChart = QuoteCandleCharter (clock)

}
