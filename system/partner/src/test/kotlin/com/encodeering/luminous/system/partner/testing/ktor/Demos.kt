package com.encodeering.luminous.system.partner.testing.ktor

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication

/**
 * @author clausen - encodeering@gmail.com
 */
internal object Demos {

    fun demo (file: String = "application-empty.conf", f: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            environment {
                config = ApplicationConfig (file)
            }

            f (this)
        }
    }

}
