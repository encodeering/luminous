package com.encodeering.luminous.system.partner.internal.io

import com.encodeering.luminous.system.partner.internal.io.Errors.quietlify
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author clausen - encodeering@gmail.com
 */
internal class FixedRateScheduler (private val repeatable: Runnable, private val rate: Long, private val unit: TimeUnit): ExecutorServiceTemplate<ScheduledExecutorService> () {

    override fun create (): ScheduledExecutorService = Executors.newScheduledThreadPool (1)

    override fun ScheduledExecutorService.job () {
        scheduleAtFixedRate (quietlify (repeatable::run), 0, rate, unit)
    }

}
