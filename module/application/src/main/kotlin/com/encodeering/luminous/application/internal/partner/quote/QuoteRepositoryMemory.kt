package com.encodeering.luminous.application.internal.partner.quote

import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.partner.quote.QuoteRepository
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentMap
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
internal class QuoteRepositoryMemory: QuoteRepository {

    private val quotes: ConcurrentMap<String, Deque<Quote>> = ConcurrentHashMap ()

    override fun remember      (isin: String) {
        quotes.computeIfAbsent (isin) { ConcurrentLinkedDeque () }
    }

    override fun forget (isin: String) {
        quotes.remove   (isin)
    }

    override fun all (isin: String): List<Quote> = quotes[isin]?.toList () ?: emptyList ()

    override fun last (isin: String): Quote? = quotes[isin]?.lastOrNull ()

    override fun add (quote: Quote): Quote = quote.also { quotes[it.isin]?.addLast (it) ?: logger.warn ("Quote ($it) couldn't be stored as there is no instrument listed for the given isin") }

    internal companion object {

        private val logger = LoggerFactory.getLogger (QuoteRepositoryMemory::class.java)

    }

}
