package com.encodeering.luminous.application.internal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author clausen - encodeering@gmail.com
 */
internal class HelloTest {

    @Test
    fun `say should return ola` () {
        assertThat (Hello.say ()).isEqualTo ("ola")
    }

}
