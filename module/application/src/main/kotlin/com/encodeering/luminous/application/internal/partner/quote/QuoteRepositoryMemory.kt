package com.encodeering.luminous.application.internal.partner.quote

import com.encodeering.luminous.application.api.io.TimeableWindowMutable
import com.encodeering.luminous.application.api.io.unpack
import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.partner.quote.QuoteRepository
import com.encodeering.luminous.application.internal.io.TimeableWindowMemory
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.temporal.ChronoUnit.MINUTES
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.enterprise.context.ApplicationScoped
import javax.inject.Named

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
@Named ("quote.repository")
internal class QuoteRepositoryMemory (private val clock: Clock): QuoteRepository {

    private val quotes: ConcurrentMap<String, TimeableWindowMutable<Quote>> = ConcurrentHashMap ()

    override fun remember      (isin: String) {
        quotes.computeIfAbsent (isin) { TimeableWindowMemory (30, MINUTES, clock) }
    }

    override fun forget (isin: String) {
        quotes.remove   (isin)
    }

    override fun all (isin: String): List<Quote> = quotes[isin]?.unpack () ?: emptyList ()

    override fun last (isin: String): Quote? = quotes[isin]?.tail

    override fun add (quote: Quote): Quote = quote.also { quotes[it.isin]?.track (it) ?: logger.warn ("Quote ($it) couldn't be stored as there is no instrument listed for the given isin") }

    internal companion object {

        private val logger = LoggerFactory.getLogger (QuoteRepositoryMemory::class.java)

    }

}
