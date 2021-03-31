package com.encodeering.luminous.application.internal.io

import com.encodeering.luminous.application.api.io.Timeable
import com.encodeering.luminous.application.api.io.unpack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.UnsupportedOperationException
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset.UTC

/**
 * @author clausen - encodeering@gmail.com
 */
internal class TimeableWindowMemoryTest {

    @Test
    fun `track should add unstale timeables` () {
        val         window = TimeableWindowMemory<TimeableTest> (clock = clock)
                    window.track (okayA)

        assertThat (window.unpack ()).containsExactly (okayA)
    }

    @Test
    fun `track should cleanup stale items` () {
        val clock = jumpclock (timestamp.toInstant (), timestamp.plusMinutes (1).toInstant ())

        val         window = TimeableWindowMemory<TimeableTest> (clock = clock)
                    window.track (okayA)
        assertThat (window.head).isEqualTo (okayA)

        clock.jump ()

                    window.track (okayB)
        assertThat (window.head).isEqualTo (okayB)
    }

    @Test
    fun `track should not add stale timeables` () {
        val         window = TimeableWindowMemory<TimeableTest> (clock = clock)
                    window.track (stale)

        assertThat (window.unpack ()).isEmpty ()
    }

    @Test
    fun `head should return the lowest timeable` () {
        val         window = TimeableWindowMemory<TimeableTest> (clock = clock)
                    window.track (okayA)
                    window.track (okayB)

        assertThat (window.head).isEqualTo (okayA)
    }

    @Test
    fun `tail should return the highest timeable` () {
        val window = TimeableWindowMemory<TimeableTest> (clock = clock)
            window.track (okayA)
            window.track (okayB)

        assertThat (window.tail).isEqualTo (okayB)
    }

    @Test
    fun `transform should cleanup before the function is applied` () {
        val clock = jumpclock (timestamp.toInstant (), timestamp.plusMinutes (1).toInstant ())

        val         window = TimeableWindowMemory<TimeableTest> (clock = clock)
                    window.track (okayA)
                    window.track (okayB)

        assertThat (window.unpack ()).containsExactly (okayA, okayB)

        clock.jump ()

        assertThat (window.unpack ()).containsExactly (okayB)
    }

    internal companion object {

        private val timestamp = OffsetDateTime.parse ("2021-03-30T13:46:19.00+02:00")

        private val clock = Clock.fixed (timestamp.toInstant (), UTC)

        private val stale = TimeableTest (timestamp.minusMinutes (30).minusNanos (1))

        private val okayA = TimeableTest (timestamp.minusMinutes (30))
        private val okayB = TimeableTest (timestamp.minusMinutes (20))

        private fun jumpclock (vararg instants: Instant) = JumpClock (UTC, instants.toList ())

    }

    internal class JumpClock (private val zone: ZoneId, private val instants: List<Instant>): Clock () {

        private var count = 0

        override fun getZone (): ZoneId = zone

        override fun withZone (zone: ZoneId?): Clock = throw UnsupportedOperationException ()

        override fun instant (): Instant = instants[count]

        fun jump () {
            count += 1
        }

    }

    internal data class TimeableTest (override val timestamp: OffsetDateTime): Timeable

}
