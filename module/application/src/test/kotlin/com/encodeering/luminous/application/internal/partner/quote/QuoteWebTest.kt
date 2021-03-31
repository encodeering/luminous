package com.encodeering.luminous.application.internal.partner.quote

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
@TestHTTPEndpoint (QuoteWeb::class)
internal class QuoteWebTest {

    @Test
    fun `candles should return a proper candle chart if the isin is known` () {
        val body = given ()
            .`when` ().get ("/VE1506683Q53/candle/1m")
            .then ()
                .statusCode (200)
                .extract ().asString ()

        assertThat (Json.decodeValue (body)).isEqualTo (Json.decodeValue ("""
        [
          {
            "open": 234.345,
            "close": 234.345,
            "low": 234.345,
            "high": 234.345,
            "opened": "2021-03-30T13:46:00+02:00",
            "closed": "2021-03-30T13:47:00+02:00"
          },
          {
            "open": 345.456,
            "close": 345.456,
            "low": 345.456,
            "high": 345.456,
            "opened": "2021-03-30T13:47:00+02:00",
            "closed": "2021-03-30T13:48:00+02:00"
          },
          {
            "open": 345.456,
            "close": 345.456,
            "low": 345.456,
            "high": 345.456,
            "opened": "2021-03-30T13:48:00+02:00",
            "closed": "2021-03-30T13:49:00+02:00"
          }
        ]
        """.trimIndent ()))
    }

    @Test
    fun `candles should return 404 otherwise` () {
        given ()
            .`when` ().get ("/VE1506683Q53xxx/candle/1m")
            .then ()
                .statusCode (404)
    }

}
