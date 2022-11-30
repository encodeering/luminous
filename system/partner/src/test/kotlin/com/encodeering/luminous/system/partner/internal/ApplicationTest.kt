package com.encodeering.luminous.system.partner.internal

import com.encodeering.luminous.system.partner.internal.ktor.events
import com.encodeering.luminous.system.partner.internal.ktor.started
import com.encodeering.luminous.system.partner.internal.ktor.stopped
import com.encodeering.luminous.system.partner.testing.ktor.Demos.demo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean

internal class ApplicationTest {

    @Test
    fun `should start and stop properly` () {
        val started = AtomicBoolean (false)
        val stopped = AtomicBoolean (false)

        demo (file = "application.conf") {
            application {
                events {
                    started { started.set (true) }
                    stopped { stopped.set (true) }
                }
            }
        }

        assertThat (started.get ()).isTrue ()
        assertThat (stopped.get ()).isTrue ()
    }

}
