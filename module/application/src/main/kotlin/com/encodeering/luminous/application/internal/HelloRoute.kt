package com.encodeering.luminous.application.internal

import org.apache.camel.builder.RouteBuilder
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
internal class HelloRoute: RouteBuilder () {

    override fun configure () {
        from ("direct:hello").routeId ("hello").to ("log:hello")
    }

}
