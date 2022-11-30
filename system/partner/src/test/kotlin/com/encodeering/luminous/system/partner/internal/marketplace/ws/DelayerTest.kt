package com.encodeering.luminous.system.partner.internal.marketplace.ws

import com.encodeering.luminous.system.partner.api.io.Messenger
import com.encodeering.luminous.system.partner.api.marketplace.Asset
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.CREATE
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase.CLOSED
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase.DIRECT
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase.RECORD
import com.encodeering.luminous.system.partner.internal.marketplace.ws.Delayer.Phase.REPLAY
import com.encodeering.luminous.system.partner.testing.playground.Assets.asset
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

/**
 * @author clausen - encodeering@gmail.com
 */
internal class DelayerTest {

    @Test
    fun `send should not pass messages to the delegatee in phase CLOSED` () {
        val messenger = messenger ()

        val          phase = AtomicReference (CLOSED)
        assertThrows<UnsupportedOperationException> {
            delayer (phase, messenger).send (message ())
        }
        assertThat  (phase.get ()).isEqualTo (CLOSED)

        verifyNoInteractions (messenger)
    }

    @Test
    fun `send should directly pass messages to the delegatee in phase DIRECT` () {
        val messenger = messenger ()
        val message   = message ()

        val         phase = AtomicReference (DIRECT)
        delayer    (phase, messenger).send (message ())
        assertThat (phase.get ()).isEqualTo (DIRECT)

        verify (messenger).send (message)
    }

    @Test
    fun `send should delay passing messages to the delegatee in phase RECORD` () {
        val messenger = messenger ()

        val         phase = AtomicReference (RECORD)
        delayer    (phase, messenger).send (message ())
        assertThat (phase.get ()).isEqualTo (RECORD)

        verify (messenger, never ()).send (any ())
    }

    @ParameterizedTest
    @ValueSource (ints = [0, 1, 2, 3, 4])
    fun `send should replay delayed (except recorded) messages to the delegatee in phase REPLAY and SWAP to phase DIRECT afterwards` (case: Int) {
        val messenger = messenger ()
        val messages  = (0..10).map (::message)

        val         phase = AtomicReference (RECORD)
        delayer    (phase, messenger).also {
            when (case) {
                0 -> {
                    messages.dropLast (1).forEach (it::send)
                }
                1 -> {
                    messages.dropLast (1).forEach (it.asRecorder ()::send)
                }
                2 -> {
                    messages.dropLast (1).forEach (it::send)
                    messages.take     (4).forEach (it.asRecorder ()::send)
                }
                3 -> {
                    messages.take     (4).forEach (it.asRecorder ()::send)
                    messages.dropLast (1).forEach (it::send)
                }
                4 -> {
                    messages.take     (4).take (2).forEach (it.asRecorder ()::send)
                    messages.dropLast (1).forEach          (it::send)
                    messages.take     (4).drop (2).forEach (it.asRecorder ()::send)
                }
            }

            phase.set (REPLAY)

            messages.takeLast (1).forEach (it::send) // a last call required to initiate the transition
        }
        assertThat (phase.get ()).isEqualTo (DIRECT)

        messages.forEach {
            verify (messenger).send (it)
        }
    }

    @Test
    fun `as recorder should directly pass messages to the delegatee in phase RECORD` () {
        val messenger = messenger ()
        val message   = message ()

        val         phase = AtomicReference (RECORD)
        delayer    (phase, messenger).asRecorder ().send (message)
        assertThat (phase.get ()).isEqualTo (RECORD)

        verify (messenger).send (message)
    }

    @ParameterizedTest
    @EnumSource (value = Phase::class, names = ["RECORD"], mode = EXCLUDE)
    fun `as recorder should fail in other phases otherwise` (phase: Phase) {
        assertThrows<IllegalStateException> {
            delayer (AtomicReference (phase), messenger ()).asRecorder ().send (message ())
        }
    }

    private companion object {

        val stamp = Instant.now ()

        fun message (ps: Int = 0, asset: Asset = asset (stamp = stamp.plusSeconds (ps.toLong ()).toString ())) = MarketMessage (CREATE, asset)

        fun messenger () = mock<Messenger<MarketMessage>> ()

        fun delayer (phase: AtomicReference<Phase>, messenger: Messenger<MarketMessage>) = Delayer (phase, messenger)

    }

}
