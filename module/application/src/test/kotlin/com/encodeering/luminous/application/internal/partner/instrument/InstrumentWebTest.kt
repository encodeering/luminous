package com.encodeering.luminous.application.internal.partner.instrument

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.vertx.core.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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

}
