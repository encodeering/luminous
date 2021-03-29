package com.encodeering.luminous.application.internal.camel

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.enterprise.event.Observes
import javax.enterprise.inject.Instance
import javax.enterprise.inject.Produces
import javax.inject.Named

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
@UnlessBuildProfile ("test")
internal class CamelModule (@Named ("camel.context") private val camel: CamelContext, routes: Instance<RouteBuilder>) {

    init {
        routes.forEach (camel::addRoutes)
    }

    @Suppress ("UNUSED_PARAMETER")
    fun onStart (@Observes ev: StartupEvent?) {
        camel.start ()
    }

    @Suppress ("UNUSED_PARAMETER")
    fun onStop (@Observes ev: ShutdownEvent?) {
        camel.stop ()
    }

}

@Dependent
internal class CamelServices {

    @Produces
    @Named ("camel.context")
    fun create (): CamelContext = DefaultCamelContext ()

}
