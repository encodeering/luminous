package com.encodeering.luminous.application.api.io

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author clausen - encodeering@gmail.com
 */
internal class ListsTest {

    @Test
    fun `reduce should drop and take as many items as specified by n if possible` () {
        assertThat (listOf (1, 2, 3).reduce (n = 2) { list, _ -> list }).containsExactly (1, 2)
        assertThat (listOf (1, 2, 3).reduce (n = 3) { list, _ -> list }).isEmpty ()
        assertThat (listOf (1, 2, 3).reduce (n = 4) { list, _ -> list }).isEmpty ()

        assertThat (listOf (1, 2, 3).reduce (n = 2) { _, item -> mutableListOf (item) }).containsExactly (3)
        assertThat (listOf (1, 2, 3).reduce (n = 3) { _, item -> mutableListOf (item) }).isEmpty ()
        assertThat (listOf (1, 2, 3).reduce (n = 4) { _, item -> mutableListOf (item) }).isEmpty ()
    }

}
