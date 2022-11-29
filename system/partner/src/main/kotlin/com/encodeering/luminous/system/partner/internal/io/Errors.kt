package com.encodeering.luminous.system.partner.internal.io

import org.slf4j.LoggerFactory
import java.lang.Thread.currentThread
import java.util.function.Supplier

/**
 * @author clausen - encodeering@gmail.com
 */
internal object Errors {

    private val logger = LoggerFactory.getLogger (Errors::class.java)

    @Suppress ("NOTHING_TO_INLINE")
    inline fun     quietly (noinline f: () -> Unit)               = quietly (f, Unit)

    inline fun <T> quietly (crossinline f: () -> T, otherwise: T): T = try {
        f ()
    } catch (e: InterruptedException) {
        currentThread ().interrupt ()
        throw e
    } catch (e: Throwable) {
        logger.warn ("Quietly ignoring: ${e.message} | ${e.stackTrace.first ()}")
        otherwise
    }

    fun     quietlify (f: Runnable): Runnable = Runnable { quietly (f::run) }

    fun <T> quietlify (f: Supplier<T>, otherwise: T): Supplier<T> = Supplier<T> { quietly (f::get, otherwise) }

}
