package com.encodeering.luminous.application.internal

import java.time.Clock
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
internal class ApplicationModule {

    @Produces
    fun clock () = Clock.systemUTC ()

}
