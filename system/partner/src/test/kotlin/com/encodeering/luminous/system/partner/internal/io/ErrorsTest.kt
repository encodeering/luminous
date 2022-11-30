package com.encodeering.luminous.system.partner.internal.io

import com.encodeering.luminous.system.partner.internal.io.Errors.quietlify
import com.encodeering.luminous.system.partner.internal.io.Errors.quietly
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Thread.currentThread
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author clausen - encodeering@gmail.com
 */
internal class ErrorsTest {

    @Test
    fun `quietly should honor interrupt exception` () {
        val called = AtomicBoolean (false)

        assertThrows<InterruptedException> {
            quietly {
                called.set (true)
                throw InterruptedException ()
            }
        }

        assertThat (currentThread ().isInterrupted).isTrue ()
        assertThat (called.get ()).isTrue ()
    }

    @Test
    fun `quietly should ignore exceptions otherwise` () {
        val called = AtomicBoolean (false)

        quietly {
            called.set (true)

            throw Throwable ()
        }

        assertThat (called.get ()).isTrue ()
    }

    @Test
    fun `quietly should not return a default on interrupts` () {
        val called = AtomicBoolean (false)

        assertThrows<InterruptedException> {
            quietly ({
                called.set (true)
                throw InterruptedException ()
            }, 42)
        }

        assertThat (currentThread ().isInterrupted).isTrue ()
        assertThat (called.get ()).isTrue ()
    }

    @Test
    fun `quietly should return a default in case of exceptions` () {
        val called = AtomicBoolean (false)

        val result = quietly ({
            called.set (true)
            throw Throwable ()
        }, 42)

        assertThat (called.get ()).isTrue ()
        assertThat (result).isEqualTo (42)
    }

    @Test
    fun `quietlify should return a runnable wrapper` () {
        val called = AtomicBoolean (false)

        assertThrows<InterruptedException> {
            quietlify {
                called.set (true)
                throw InterruptedException ()
            }.run ()
        }

        assertThat (currentThread ().isInterrupted).isTrue ()
        assertThat (called.get ()).isTrue ()
    }

    @Test
    fun `quietlify should return a supplier wrapper` () {
        val called = AtomicBoolean (false)

        val result = quietlify ({
            called.set (true)
            throw Throwable ()
        }, 42).get ()

        assertThat (called.get ()).isTrue ()
        assertThat (result).isEqualTo (42)
    }

}
