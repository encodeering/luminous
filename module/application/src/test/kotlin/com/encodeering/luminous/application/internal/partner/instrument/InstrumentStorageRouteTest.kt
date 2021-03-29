package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import com.encodeering.luminous.application.test.camel.launch
import org.apache.camel.CamelExecutionException
import org.apache.camel.RoutesBuilder
import org.apache.camel.test.junit5.CamelTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

/**
 * @author clausen - encodeering@gmail.com
 */
internal class InstrumentStorageRouteTest: CamelTestSupport () {

    private val instruments: InstrumentRepository = mock (InstrumentRepository::class.java, RETURNS_DEEP_STUBS)

    override fun isUseAdviceWith () = true

    override fun createRouteBuilder (): RoutesBuilder = InstrumentStorageRoute (instruments)

    @Test
    fun `messages should be addable to the repository` () = context.launch {
        template.sendBodyAndHeaders ("direct:track-instrument", instrument, operations.add)

        verify (instruments).add (instrument)
    }

    @Test
    fun `messages should be removable from the repository` () = context.launch {
        template.sendBodyAndHeaders ("direct:track-instrument", instrument, operations.delete)

        verify (instruments).remove (instrument)
    }

    @Test
    fun `instruments will throw an exception if type is invalid` () = context.launch {
        val exception = assertThrows<CamelExecutionException> {
            template.sendBodyAndHeaders ("direct:track-instrument", instrument, mapOf ("operation" to ""))
        }

        assertThat (exception.cause).isInstanceOf (IllegalStateException::class.java)
    }

    internal companion object {

        private object operations {

            val delete = mapOf ("operation" to InstrumentMessage.Type.DELETE)
            val add    = mapOf ("operation" to InstrumentMessage.Type.ADD)

        }

        private val instrument = Instrument ("VE1506683Q53", "hello")

    }

}
