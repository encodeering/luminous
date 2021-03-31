package com.encodeering.luminous.application.api.io

import java.time.OffsetDateTime

interface Timeable {

    val timestamp: OffsetDateTime

}

interface TimeableWindow<T: Timeable> {

    val head: T?
    val tail: T?

    fun <R> transform (f: (List<T>) -> R): R

}

interface TimeableWindowMutable<T: Timeable>: TimeableWindow<T> {

    fun track (timeable: T)

}

fun <T: Timeable> TimeableWindow<T>.unpack () = transform { it }
