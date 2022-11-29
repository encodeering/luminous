package com.encodeering.luminous.system.partner.api.io

import java.io.Closeable

/**
 * @author clausen - encodeering@gmail.com
 */
fun <T> Closeable.closeIf (f: () -> T) {
    try {
        f ()
    } catch (e: Throwable) {
        close ()
        throw e
    }
}
