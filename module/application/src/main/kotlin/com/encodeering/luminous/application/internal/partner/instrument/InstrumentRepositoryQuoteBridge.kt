package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import com.encodeering.luminous.application.api.partner.quote.QuoteRepository
import javax.annotation.Priority
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Alternative
import javax.inject.Named

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
@Alternative
@Priority (1)
internal class InstrumentRepositoryQuoteBridge (
    @Named ("instrument.repository") private val instruments: InstrumentRepository,
    @Named ("quote.repository")      private val quotes: QuoteRepository
): InstrumentRepository by instruments {

    override fun add           (instrument: Instrument): Instrument {
               quotes.remember (instrument.isin)
        return instruments.add (instrument)
    }

    override fun remove           (instrument: Instrument): Instrument? {
               quotes.forget      (instrument.isin)
        return instruments.remove (instrument)
    }

}
