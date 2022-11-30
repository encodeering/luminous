package com.encodeering.luminous.system.partner.internal.marketplace

import com.encodeering.luminous.system.partner.api.marketplace.Activity
import com.encodeering.luminous.system.partner.api.marketplace.Activity.HIGH
import com.encodeering.luminous.system.partner.api.marketplace.Activity.LOW
import com.encodeering.luminous.system.partner.api.marketplace.Asset
import com.encodeering.luminous.system.partner.api.marketplace.Notifier
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.*
import com.encodeering.luminous.system.partner.internal.marketplace.MarketplaceDefault.Companion.activity
import com.encodeering.luminous.system.partner.internal.marketplace.MarketplaceDefault.Companion.description
import com.encodeering.luminous.system.partner.internal.marketplace.MarketplaceDefault.Companion.isin
import com.encodeering.luminous.system.partner.internal.marketplace.MarketplaceDefault.Companion.price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verifyNoMoreInteractions
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MarketplaceDefaultTest {

    @ParameterizedTest
    @MethodSource ("random")
    fun `isin should generate a syntactically correct identifier` (random: Random) {
        repeat (50) {
            assertThat (isin (random)).matches ("""[A-Z]{2}[0-9]{10}""".toRegex ().toPattern ())
        }
    }

    @ParameterizedTest
    @MethodSource ("random")
    fun `description should generate a lorem-ipsum alike text` (random: Random) {
        repeat (50) {
            val description = description (random)

            assertThat (description).matches ("""[a-z ]*""".toRegex ().toPattern ())

            assertThat (description).doesNotMatch (""" {2,}""".toRegex ().toPattern ())
            assertThat (description).doesNotMatch ("""^ .* $""".toRegex ().toPattern ())
        }
    }

    @ParameterizedTest
    @MethodSource ("pricebits")
    fun `price should generate a number, fluctuating by certain percentage range` (next: Double) {
        val random = Pinned.double (next)

        val         base = random.nextDouble () * 100
        val lower = base * 0.985
        val upper = base * 1.015

        assertThat (price (random)).isBetween       (lower, upper)
        assertThat (price (random, base)).isBetween (lower, upper)
    }

    @ParameterizedTest
    @MethodSource ("random")
    fun `activity should generate all activities` (random: Random) {
        assertThat (generateSequence { activity (random) }.take (100).toSet ()).containsAll (Activity.values ().toSet ())
    }

    @Test
    fun `activity should generate lows with a certain probability` () {
        val random = Pinned.double (0.22222)

        assertThat (generateSequence { activity (random) }.take (100).toSet ()).containsOnly (LOW)
    }

    @Test
    fun `activity should generate highs with a certain probability` () {
        val random = Pinned.double (0.77777)

        assertThat (generateSequence { activity (random) }.take (100).toSet ()).containsOnly (HIGH)
    }

    @Test
    fun `stimulate should perform a create, update and delete loop` () {
        val notifier = mock<Notifier> ()
        val marketplace = marketplace (Config (instruments = Config.Instrument (max = 1, create = 1.0, delete = 1.0), random = Pinned.double (0.0)))

        marketplace.subscribe (notifier)

        repeat (50) {
            try {
                marketplace.stimulate ()

                inOrder (notifier).run {
                    val asset = Asset ("FU1212109727", "", 0.0, LOW, stamp)

                    verify (notifier).notify (CREATE, asset)
                    verify (notifier).notify (UPDATE, asset)
                    verify (notifier).notify (DELETE, asset)
                    verifyNoMoreInteractions (notifier)
                }
            } finally {
                reset (notifier)
            }
        }
    }

    @Test
    fun `stimulate should notify properly` () {
        val notifier = mock<Notifier> ()

        val marketplace = marketplace (Config (instruments = Config.Instrument (max = 10, create = 1.0, delete = 0.0), random = Pinned.double (0.0)))
            marketplace.subscribe (notifier)

        repeat (50) {
            marketplace.stimulate ()
        }

        verify (notifier, times (10)).notify (eq (CREATE), any ())
        verify (notifier, times (55 + 40 * 10)).notify (eq (UPDATE), any ()) /* 1 + 2 + 3 ... + 10 = 55 = 11 * 5 */
        verify (notifier, never ()).notify (eq (DELETE), any ())
    }

    @Test
    fun `stimulate should delete properly` () {
        val notifier = mock<Notifier> ()

        val marketplace = marketplace (Config (instruments = Config.Instrument (max = 10, create = 0.5, delete = 1.0), random = Pinned.double (0.0)))
            marketplace.subscribe (notifier)

        repeat (50) {
            marketplace.stimulate ()
        }

        verify (notifier, times (50)).notify (eq (DELETE), any ())
    }

    @Test
    fun `subscribe should deregister properly` () {
        val notifiers = mutableListOf<Notifier> ()

        val marketplace = marketplace (Config (instruments = Config.Instrument (max = 1, create = 1.0, delete = 1.0), random = Pinned.double (0.0)))

        repeat (50) {
            val          notifier = mock<Notifier> ()
            notifiers += notifier

            val subscription = marketplace.subscribe (notifier)

            try {
                marketplace.stimulate ()

                verify (notifier, times (3)).notify (any (), any ())
            } finally {
                subscription.run ()
            }
        }

        notifiers.forEach { verifyNoMoreInteractions (it) }
    }

    @Test
    fun `subscribe should handle multiple subscriptions at the same time` () {
        val notifiers = mutableListOf<Notifier> ()
        val subscriptions = mutableListOf<Runnable> ()

        val marketplace = marketplace (Config (instruments = Config.Instrument (max = 1, create = 1.0, delete = 1.0), random = Pinned.double (0.0)))

        repeat (50) {
            val          notifier = mock<Notifier> ()
            notifiers += notifier

            subscriptions += marketplace.subscribe (Notifier (notifier::notify)) // interesting case with hashCode vs identifyHashCode
        }

        try {
            marketplace.stimulate ()

            notifiers.forEach {
                verify (it, times (3)).notify (any (), any ())
            }
        } finally {
            subscriptions.forEach { it.run () }
        }
    }

    @Test
    fun `dump should emit all known create and update notifications` () {
        val expectation = NotifierMap ()

        val marketplace = marketplace (Config (instruments = Config.Instrument (max = 50, create = 1.0, delete = 0.0), random = Pinned.double (0.0)))
            marketplace.subscribe (expectation)

        var size = 1

        repeat (50) {
            marketplace.stimulate ()

            val               result = NotifierMap ()
            marketplace.dump (result)
            assertThat       (result.received).containsExactlyInAnyOrderEntriesOf (expectation.received)
            assertThat       (result.received.size).isEqualTo (size)

            size += 1
        }
    }

    @Test
    fun `dump should emit a create and update event` () {
        val marketplace = marketplace (Config (instruments = Config.Instrument (max = 50, create = 1.0, delete = 0.0), random = Pinned.double (0.0)))

        var size = 1

        repeat (50) {
            marketplace.stimulate ()

            val               result = mock<Notifier> {  }
            marketplace.dump (result)

            inOrder (result).run {
                repeat (size) {
                    verify (result).notify (eq (CREATE), any ())
                    verify (result).notify (eq (UPDATE), any ())
                }
                verifyNoMoreInteractions ()

                size += 1
            }
        }
    }

    private object Pinned {

        fun double (fix: Double, real: Random = Random (42)): Random = object: Random () {

            init {
                check (fix in 0.0..1.0)
            }

            override fun nextBits (bitCount: Int): Int = real.nextBits (bitCount)

            override fun nextDouble (): Double = fix

        }

    }

    private companion object {

        val stamp = Instant.now ()

        val clock = Clock.fixed (stamp, UTC)

        fun marketplace (config: Config = Config ()) = MarketplaceDefault (clock, config)

        @JvmStatic
        fun random () = listOf (Arguments.of (Random (42))) // give a newly generated pseudo random to every test

        @JvmStatic
        fun pricebits (): List<Arguments> = 100.let { n -> generateSequence (0.0) { it + 1 }.take (n).map { Arguments.of (it / n.toDouble ()) }.toList () }

    }

    private class NotifierMap: Notifier {

        val received = ConcurrentHashMap<String, Asset> ()

        override fun notify (code: Notifier.Code, asset: Asset) {
            when (code) {
                CREATE,
                UPDATE -> received[asset.isin] = asset
                DELETE -> received.remove (asset.isin)
            }
        }

    }

}
