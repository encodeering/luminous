package com.encodeering.luminous.application.internal.io

import com.encodeering.luminous.application.api.io.Timeable
import com.encodeering.luminous.application.api.io.TimeableWindowMutable
import java.time.Clock
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

internal class TimeableWindowMemory<T: Timeable> (private val window: Int = 30, private val unit: ChronoUnit = MINUTES, private val clock: Clock = Clock.systemUTC ()): TimeableWindowMutable<T> {

    private val timeables: NavigableSet<T> = ConcurrentSkipListSet (compareBy (Timeable::timestamp))

    override val head: T? get () = timeables.firstOrNull ()
    override val tail: T? get () = timeables.lastOrNull ()

    override fun track (timeable: T) {
        if             (timeable.stale ()) return

        timeables.add  (timeable)
        timeables.cleanup ()
    }

    override fun <R> transform (f: (List<T>) -> R): R = f (timeables.cleanup ().toList ())

    private fun NavigableSet<T>.cleanup (): NavigableSet<T> {
        firstOrNull { ! it.stale () }?.let { headSet (it).clear () }

        return this
    }

    private fun T.stale () = timestamp.plus (window.toLong (), unit).isBefore (clock.instant ().atOffset (UTC))

}
