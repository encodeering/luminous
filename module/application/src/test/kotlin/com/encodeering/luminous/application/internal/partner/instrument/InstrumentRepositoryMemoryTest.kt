package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author clausen - encodeering@gmail.com
 */
internal class InstrumentRepositoryMemoryTest {

    @Test
    fun `all should return all known instruments` () {
        val         instruments = InstrumentRepositoryMemory ()

        assertThat (instruments.all ()).isEmpty ()

                    instruments.add (instrumentV)
                    instruments.add (instrumentL)

        assertThat (instruments.all ()).containsExactlyInAnyOrder (instrumentL, instrumentV)
    }

    @Test
    fun `one should return a known instrument` () {
        val         instruments = InstrumentRepositoryMemory ()

                    instruments.add (instrumentV)
                    instruments.add (instrumentL)

        assertThat (instruments.one (instrumentL.isin)).isEqualTo (instrumentL)
    }

    @Test
    fun `one should return nothing otherwise` () {
        val         instruments = InstrumentRepositoryMemory ()

        assertThat (instruments.one (instrumentV.isin)).isNull ()
        assertThat (instruments.one (instrumentL.isin)).isNull ()
    }

    @Test
    fun `add should add an instrument` () {
        val         instruments = InstrumentRepositoryMemory ()

        assertThat (instruments.add (instrumentV)).isEqualTo (instrumentV)
        assertThat (instruments.add (instrumentL)).isEqualTo (instrumentL)
    }

    @Test
    fun `remove should remove a known instrument` () {
        val         instruments = InstrumentRepositoryMemory ()

                    instruments.add (instrumentV)

        assertThat (instruments.remove (instrumentV)).isEqualTo (instrumentV)
        assertThat (instruments.remove (instrumentV)).isEqualTo (null)
        assertThat (instruments.remove (instrumentL)).isEqualTo (null)
    }

    internal companion object {

        private val instrumentV = Instrument ("VE1506683Q53", "hello")
        private val instrumentL = Instrument ("LF681P504335", "world")

    }

}
