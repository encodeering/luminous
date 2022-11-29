package com.encodeering.luminous.system.partner.api.io

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

/**
 * @author clausen - encodeering@gmail.com
 */
interface Service {

    fun start ()

    fun stop (await: Long = 1, unit: TimeUnit = SECONDS)

    fun stopped (): Boolean

}

fun <T: Service> T.use (autostart: Boolean = false, f: T.() -> Unit) {
    try {
        if (autostart)
            start ()

        f ()
    } finally {
        stop ()
    }
}
