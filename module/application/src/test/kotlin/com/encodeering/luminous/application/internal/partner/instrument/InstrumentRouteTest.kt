package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.test.camel.launch
import com.encodeering.luminous.application.test.partner.PartnerConfigMemory
import com.encodeering.luminous.application.test.partner.PartnerContainer
import org.apache.camel.Exchange
import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.testcontainers.junit5.ContainerAwareTestSupport
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author clausen - encodeering@gmail.com
 */
internal class InstrumentRouteTest: ContainerAwareTestSupport () {

    val config by lazy { PartnerConfigMemory (URI.create ("${getContainerHost ("partner-instruments-test")}:${getContainerPort ("partner-instruments-test", 8080)}")) }

    override fun isUseAdviceWith () = true

    override fun createContainer (): GenericContainer<*> = PartnerContainer ("partner-instruments-test")

    override fun createRouteBuilders (): Array<RoutesBuilder> = arrayOf (MockRoute (), InstrumentRoute (config))

    @Test
    fun `instruments should be read from the stream` () {
        val mock = getMockEndpoint ("mock:track-instrument")
            mock.expectedMinimumMessageCount (1)
            mock.expectedMessagesMatches ({
                exchange: Exchange ->
                exchange.message.body is Instrument &&
                exchange.message.getHeader ("operation") is InstrumentMessage.Type
            })

        context.launch {
            assertMockEndpointsSatisfied (1, SECONDS)
        }
    }

    private class MockRoute: RouteBuilder () {

        override fun configure () {
            from ("direct:track-instrument").to ("mock:track-instrument")
        }

    }

}
