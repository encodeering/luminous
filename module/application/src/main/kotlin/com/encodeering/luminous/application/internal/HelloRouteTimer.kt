package com.encodeering.luminous.application.internal

import org.apache.camel.builder.RouteBuilder
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
internal class HelloRouteTimer: RouteBuilder () {

    override fun configure () {
        from ("timer:hello?period=1000").to ("direct:hello")
    }

}
