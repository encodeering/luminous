package com.encodeering.luminous.application.internal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock

/**
 * @author clausen - encodeering@gmail.com
 */
internal class ApplicationModuleTest {

    @Test
    fun `clock should provide a clock based on system utc` () {
        assertThat (ApplicationModule ().clock ()).isEqualTo (Clock.systemUTC ())
    }

}
