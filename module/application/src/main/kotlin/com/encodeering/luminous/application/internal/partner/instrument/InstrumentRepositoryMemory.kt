package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.enterprise.context.ApplicationScoped
import javax.inject.Named

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
@Named ("instrument.repository")
internal class InstrumentRepositoryMemory: InstrumentRepository {

    private val instruments: ConcurrentMap<String, Instrument> = ConcurrentHashMap ()

    override fun all (): Set<Instrument> = instruments.values.toSet ()

    override fun one (isin: String): Instrument? = instruments[isin]

    override fun add  (instrument: Instrument): Instrument = instrument.also { instruments[it.isin] = it }

    override fun remove (instrument: Instrument): Instrument? = instruments.remove (instrument.isin)

}
