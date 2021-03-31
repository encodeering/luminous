package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import com.encodeering.luminous.application.api.partner.quote.Quote
import com.encodeering.luminous.application.api.partner.quote.QuoteRepository
import com.encodeering.luminous.application.internal.partner.quote.QuoteRepositoryMemory
import io.quarkus.arc.AlternativePriority
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.vertx.core.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.OffsetDateTime
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces

/**
 * @author clausen - encodeering@gmail.com
 */
@QuarkusTest
@TestHTTPEndpoint (InstrumentWeb::class)
internal class InstrumentWebTest {

    @Test
    fun `index should return all instruments` () {
        val body = given ()
            .`when` ().get ("/")
            .then ()
                .statusCode (200)
                .extract ().asString ()

        assertThat (Json.decodeValue (body)).isEqualTo (Json.decodeValue ("""
        [
          {
            "isin": "VE1506683Q53",
            "description": "hello",
            "price": {
              "last": {
                "price": 345.456,
                "timestamp": "2021-03-30T13:47:19+02:00"
              }
            }
          },
          {
            "isin": "LF681P504335",
            "description": "world",
            "price": null
          }
        ]
        """.trimIndent ()))
    }

    @Test
    fun `isin child should return a known instrument` () {
        val body = given ()
            .`when` ().get ("/VE1506683Q53")
            .then ()
                .statusCode (200)
                .extract ().asString ()

        assertThat (Json.decodeValue (body)).isEqualTo (Json.decodeValue ("""
        {
          "isin": "VE1506683Q53",
          "description": "hello",
          "price": {
            "last": {
              "price": 345.456,
              "timestamp": "2021-03-30T13:47:19+02:00"
            }
          }
        }
        """.trimIndent ()))
    }

    @Test
    fun `isin child should return 404 otherwise` () {
        given ()
            .`when` ().get ("/VE1506683Q53xxx")
            .then ()
                .statusCode (404)
    }

    @ApplicationScoped
    internal class InstrumentRepositoryTestProvider {

        private val timestamp = OffsetDateTime.parse ("2021-03-30T13:46:19.00+02:00")

        @Produces
        @AlternativePriority (2)
        fun instruments (): InstrumentRepository = InstrumentRepositoryMemory ().apply {
            add (Instrument ("VE1506683Q53", "hello"))
            add (Instrument ("LF681P504335", "world"))
        }

        @Produces
        @AlternativePriority (2)
        fun quotes (): QuoteRepository = QuoteRepositoryMemory (Clock.fixed (timestamp.plusMinutes (2).toInstant (), timestamp.offset)).apply {
            remember   ("VE1506683Q53")

            add (Quote ("VE1506683Q53", "234.345".toBigDecimal (), timestamp))
            add (Quote ("VE1506683Q53", "345.456".toBigDecimal (), timestamp.plusMinutes (1)))
        }

    }

}
