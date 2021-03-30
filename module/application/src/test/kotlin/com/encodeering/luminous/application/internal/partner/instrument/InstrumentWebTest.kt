package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import io.quarkus.test.Mock
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.vertx.core.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
            "description": "hello"
          },
          {
            "isin": "LF681P504335",
            "description": "world"
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
          "description": "hello"
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

        @Produces
        @Mock
        fun instruments (): InstrumentRepository = InstrumentRepositoryMemory ().apply {
            add (Instrument ("VE1506683Q53", "hello"))
            add (Instrument ("LF681P504335", "world"))
        }

    }

}
