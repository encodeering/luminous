package com.encodeering.luminous.system.partner.testing.io

import com.encodeering.luminous.system.partner.api.io.Messenger
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch

/**
 * @author clausen - encodeering@gmail.com
 */
internal class RecordingMessenger<T> (calls: Int, private val f: (T) -> Boolean = { true }): Messenger<T> {

    private val latch = CountDownLatch (calls)

    private val recordings = ConcurrentLinkedQueue<T> ()

    override fun send (content: T) {
        if (! f       (content)) return

        latch.countDown ()

        recordings += content
    }

    fun recordings (): List<T> = recordings.toList ()

    fun await () = latch.await ()

}
