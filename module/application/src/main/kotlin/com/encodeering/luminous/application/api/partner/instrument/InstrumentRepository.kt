package com.encodeering.luminous.application.api.partner.instrument

/**
 * @author clausen - encodeering@gmail.com
 */
data class Instrument (val isin: String, val description: String)

interface InstrumentRepository {

    fun all (): Set<Instrument>

    fun one (isin: String): Instrument?

    fun add (instrument: Instrument): Instrument

    fun remove (instrument: Instrument): Instrument?

}
