package com.encodeering.luminous.application.test.camel

import org.apache.camel.CamelContext

/**
 * @author clausen - encodeering@gmail.com
 */
fun CamelContext.launch (f: () -> Unit) {
    start ()

    try {
        f ()
    } finally {
        stop ()
    }
}
