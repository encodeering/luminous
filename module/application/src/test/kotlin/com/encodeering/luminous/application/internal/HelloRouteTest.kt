package com.encodeering.luminous.application.internal

import com.encodeering.luminous.application.test.camel.launch
import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.AdviceWith.adviceWith
import org.apache.camel.test.junit5.CamelTestSupport
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author clausen - encodeering@gmail.com
 */
internal class HelloRouteTest: CamelTestSupport () {

    override fun isUseAdviceWith () = true

    override fun createRouteBuilder (): RoutesBuilder = HelloRoute ()

    @Test
    fun `hello should be logged` () {
        adviceWith (context, "hello") {
            a ->
            a.mockEndpoints ("log:*")
        }

        val mock = getMockEndpoint ("mock:log:hello")
            mock.expectedBodiesReceived ("ola")

        context.launch {
            template.sendBody ("direct:hello", "ola")

            assertMockEndpointsSatisfied (1, SECONDS)
        }
    }

}
