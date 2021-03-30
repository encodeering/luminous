package com.encodeering.luminous.application.internal.partner.integration

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
import java.time.Clock
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author clausen - encodeering@gmail.com
 */
internal class PartnerRouteTest: ContainerAwareTestSupport () {

    val config by lazy { PartnerConfigMemory (URI.create ("${getContainerHost ("partner-instruments-test")}:${getContainerPort ("partner-instruments-test", 8080)}")) }

    override fun isUseAdviceWith () = true

    override fun createContainer (): GenericContainer<*> = PartnerContainer ("partner-instruments-test")

    override fun createRouteBuilders (): Array<RoutesBuilder> = arrayOf (MockRoute (), PartnerRoute (config, Clock.systemUTC ()))

    @Test
    fun `data should be read from the stream` () {
        val mock = getMockEndpoint ("mock:partner-track-record")
            mock.expectedMinimumMessageCount (1)
            mock.expectedMessagesMatches ({
                exchange: Exchange ->
                exchange.message.getHeader ("operation") in InstrumentMessage.Type.values ().toList () + QuoteMessage.Type.values ().toList ()
            })

        context.launch {
            assertMockEndpointsSatisfied (1, SECONDS)
        }
    }

    private class MockRoute: RouteBuilder () {

        override fun configure () {
            from ("direct:partner-track-record").to ("mock:partner-track-record")
        }

    }

}
