package com.encodeering.luminous.system.partner.internal.io

import com.encodeering.luminous.system.partner.api.io.use
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author clausen - encodeering@gmail.com
 */
internal class FixedRateSchedulerTest {

    @Test
    fun `runs the given command quietly and repeatedly on start` () {
        val entered = CountDownLatch (3)

        FixedRateScheduler ({
            entered.countDown ()

            throw IllegalStateException ("Something went wrong")
        }, 10, MILLISECONDS).use {
            start ()

            entered.await ()
        }

        assertThat (entered.count).isZero ()
    }

    @Test
    fun `stops executing the given command on stop` () {
        val called = AtomicBoolean (false)

        val entered = CountDownLatch (1)
        val idle = CountDownLatch (1)

        FixedRateScheduler ({
            entered.countDown ()

            try {
                idle.await ()
            } catch (e: InterruptedException) {
                called.set (true)
            }
        }, 10, MILLISECONDS).use {
            start ()

            entered.await ()

            stop (50, MILLISECONDS)
        }

        assertThat (called.get ()).isTrue ()
    }

    @Test
    fun `starts only once` () {
        FixedRateScheduler ({}, 1, SECONDS).use {
            assertThat (stopped ()).isTrue ()

            start ()

            assertThrows<IllegalStateException> {
                start ()
            }
        }
    }

}
