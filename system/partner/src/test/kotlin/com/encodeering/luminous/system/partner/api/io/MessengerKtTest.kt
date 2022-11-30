package com.encodeering.luminous.system.partner.api.io

import com.encodeering.luminous.system.partner.testing.co.TestNumber
import com.encodeering.luminous.system.partner.testing.co.TestNumberDefault
import com.encodeering.luminous.system.partner.testing.co.asScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.coroutines.coroutineContext

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MessengerKtTest {

    @Test
    fun `as messenger should run the send function within the given scope` () {
        val numbers = Channel<Int> (1)

        val messenger = MessengerCo<Int> {
            numbers.send (
                checkNotNull (coroutineContext[TestNumber]).number + it
            )
        }.asMessenger (TestNumberDefault (42).asScope ())

        repeat (10) {
            messenger.send (it)

            runBlocking {
                assertThat (numbers.receive ()).isEqualTo (42 + it)
            }
        }
    }

}
