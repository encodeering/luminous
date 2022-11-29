package com.encodeering.luminous.system.partner.internal.marketplace

import com.encodeering.luminous.system.partner.api.marketplace.Activity
import com.encodeering.luminous.system.partner.api.marketplace.Activity.HIGH
import com.encodeering.luminous.system.partner.api.marketplace.Activity.LOW
import com.encodeering.luminous.system.partner.api.marketplace.Activity.MEDIUM
import com.encodeering.luminous.system.partner.api.marketplace.Asset
import com.encodeering.luminous.system.partner.api.marketplace.Marketplace
import com.encodeering.luminous.system.partner.api.marketplace.Notifier
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.CREATE
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.DELETE
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.UPDATE
import com.encodeering.luminous.system.partner.internal.io.Errors.quietly
import java.lang.Integer.min
import java.lang.System.identityHashCode
import java.time.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.random.Random

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MarketplaceDefault (private val clock: Clock, private val config: Config): Marketplace {

    private val isins = generateSequence { isin (config.random) }.take (config.instruments.max).toSet ()

    private val assets: ConcurrentMap<String, Asset> = ConcurrentHashMap ()

    private val subscriptions: ConcurrentMap<Int, Notifier> = ConcurrentHashMap ()

    private val notifier = Notifier {                           code, asset ->
        subscriptions.values.forEach { n -> quietly { n.notify (code, asset) } }
    }

    // let the market shift a bit with a good old stimulus or was it a (matrix) simulation, who knows?
    @Synchronized
    override fun stimulate () {
        create ()
        update ()
        delete ()
    }

    override fun dump (notifier: Notifier) {
        assets.values.forEach { // a dump is not a stop the world operation
            notifier.notify (CREATE, it)
            notifier.notify (UPDATE, it)
        }
    }

    override fun subscribe                                        (notifier: Notifier): Runnable {
        val                                key = identityHashCode (notifier)
        subscriptions +=                   key to notifier
        return Runnable { subscriptions -= key } // may unsubscribe at any time
    }

    private fun create () {
        with (config) {
            val unused = isins - assets.keys
            if (unused.isEmpty () || ! passes (instruments.create))
                return@with

            val       asset = Asset (unused.random (random), description (random), price (random), activity (random), clock.instant ())
            assets += asset.isin to asset

            notifier.notify (CREATE, asset)
        }
    }

    private fun update () {
        with (config) {
            assets.values.filter { passes (it.activity.probability) }.forEach {
                val       next = it.copy (price = price (random, it.price), stamp = clock.instant ())
                assets += next.isin to next

                notifier.notify (UPDATE, next)
            }
        }
    }

    private fun delete () {
        with (config) {
            val used = assets.keys
            if (used.isEmpty () || ! passes (instruments.delete))
                return@with

            val                      asset = assets.remove (used.random (random))!!
            notifier.notify (DELETE, asset.copy (stamp = clock.instant ()))
        }
    }

    internal companion object {

        // syntactically (+), semantically (-)
        fun isin (random: Random): String = "${random.nextInt ('A'.code, 'Z'.code + 1).toChar ()}${random.nextInt ('A'.code, 'Z'.code + 1).toChar ()}${generateSequence { random.nextInt (0, 9 + 1) }.take (10).joinToString ("")}"

        // generates a random text that is not that nicely readable as lorem ipsum
        fun description (random: Random): String = generateSequence {
            if (random.nextDouble () < 0.10)
                ' '
            else
                random.nextInt ('a'.code, 'z'.code + 1).toChar ()
        }
            .take (10 + min (random.nextInt (20), random.nextInt (40)))
            .joinToString ("")
            .replace ("""\s{2,}""".toRegex (), " ")
            .trim ()

        // generates a price that is allowed to fluctuate by a certain percentage
        fun price (random: Random, price: Double = random.nextDouble () * 100) = random.nextDouble (0.985, 1.015) * price

        // generates an activity with medium favored against low and high
        fun activity (random: Random): Activity = random.nextDouble ().let {
            when {
                it <= 0.22222 -> LOW
                it >= 0.77777 -> HIGH
                else          -> MEDIUM
            }
        }

    }

}

internal data class Config (
    val instruments: Instrument = Instrument (),
    val random: Random = Random (2345123)
) {

    data class Instrument (
       val max: Int = 250,
       val create: Double = 0.1250,
       val delete: Double = 0.0125,
    )

    fun passes (probability: Double): Boolean = random.nextDouble () < probability
}
