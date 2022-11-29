package com.encodeering.luminous.system.partner.api.io

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author clausen - encodeering@gmail.com
 */
internal class CloseableKtTest {

    @Test
    fun `should auto-close if the given function errors` () {
        val called = AtomicBoolean (false)

        val error = assertThrows<Throwable> {
            Closeable { called.set (true) }.closeIf { throw Throwable ("ouch")  }
        }

        assertThat (called.get ()).isTrue ()
        assertThat (error.message).isEqualTo ("ouch")
    }

    @Test
    fun `should not auto-close otherwise` () {
        val called = AtomicBoolean (false)

        Closeable { throw Throwable ("ouch") }.closeIf { called.set (true)  }

        assertThat (called.get ()).isTrue ()
    }

}
