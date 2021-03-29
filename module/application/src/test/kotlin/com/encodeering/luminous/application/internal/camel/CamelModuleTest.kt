package com.encodeering.luminous.application.internal.camel

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import javax.enterprise.inject.Instance

/**
 * @author clausen - encodeering@gmail.com
 */
internal class CamelModuleTest {

    @Test
    fun `init should bind all routes` () {
        val camel = camel ()
        val a = TestRoute ()
        val b = TestRoute ()

        CamelModule (camel, routes (a, b))

        verify (camel).addRoutes (a)
        verify (camel).addRoutes (b)
    }

    @Test
    fun `startup should start camel` () {
        val camel = camel ()

        CamelModule (camel, routes ()).onStart (StartupEvent ())

        verify (camel).start ()
    }

    @Test
    fun `shutdown should stop camel` () {
        val camel = camel ()

        CamelModule (camel, routes ()).onStop (ShutdownEvent ())

        verify (camel).stop ()
    }

    @Test
    fun `bean should not be active on the test profile` () {
        assertThat (CamelModule::class.java.getAnnotation (UnlessBuildProfile::class.java).value).isEqualTo ("test")
    }

    internal companion object {

        fun camel (): CamelContext = Mockito.mock (CamelContext::class.java, RETURNS_DEEP_STUBS)

        @Suppress ("UNCHECKED_CAST")
        fun routes (vararg routes: RouteBuilder): Instance<RouteBuilder> = (Mockito.mock (Instance::class.java) as Instance<RouteBuilder>).also {
            `when` (it.iterator ()).thenReturn (routes.asIterable ().toMutableList ().iterator ())
        }

    }

    internal class TestRoute: RouteBuilder () {

        override fun configure () {}

    }

}
