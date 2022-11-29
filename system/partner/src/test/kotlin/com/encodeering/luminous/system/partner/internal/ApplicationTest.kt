package com.encodeering.luminous.system.partner.internal

import com.encodeering.luminous.system.partner.testing.ktor.Demos.demo
import org.junit.jupiter.api.Test

internal class ApplicationTest {

    @Test
    fun `should start and stop properly` () {
        demo (file = "application.conf") {}
    }

}
