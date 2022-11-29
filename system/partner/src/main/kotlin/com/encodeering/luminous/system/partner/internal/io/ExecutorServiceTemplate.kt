package com.encodeering.luminous.system.partner.internal.io

import com.encodeering.luminous.system.partner.api.io.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author clausen - encodeering@gmail.com
 */
internal abstract class ExecutorServiceTemplate<T: ExecutorService>: Service {

    private var executor: T? = null

    @Synchronized
    override fun start () {
        check (stopped ())

        executor = create ().apply { job () }
    }

    @Synchronized
    override fun stop (await: Long, unit: TimeUnit) {
        val e = executor ?: return

        try {
            e.shutdown ()
            e.awaitTermination (await / 2, unit) // may not be too accurate in every constellation, a conversion to a smaller unit should be done
            e.shutdownNow ()
            e.awaitTermination (await / 2, unit)
        } finally {
            executor = null
        }
    }

    @Synchronized
    override fun stopped (): Boolean = executor == null

    abstract fun create (): T

    abstract fun T.job ()

}
