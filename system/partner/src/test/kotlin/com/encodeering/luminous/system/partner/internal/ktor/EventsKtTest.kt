package com.encodeering.luminous.system.partner.internal.ktor

import com.encodeering.luminous.system.partner.testing.ktor.Demos.demo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author clausen - encodeering@gmail.com
 */
internal class EventsKtTest {

    @Test
    fun `events should be called within the corresponding phase` () {
        val call = AtomicInteger (0)
        val called = ConcurrentHashMap<String, Int> ()

        demo {
            application {
                events {
                    stopped      { called += "stopped"      to call.incrementAndGet () }
                    stopping     { called += "stopping"     to call.incrementAndGet () }
                    decommission { called += "decommission" to call.incrementAndGet () }
                    started      { called += "started"      to call.incrementAndGet () }
                }
            }
        }

        assertThat (called).containsExactlyInAnyOrderEntriesOf (mapOf (
            "started"      to 1,
            "decommission" to 2,
            "stopping"     to 3,
            "stopped"      to 4
        ))
    }

}
