package com.encodeering.luminous.application.internal.partner.integration

import com.encodeering.luminous.application.internal.partner.integration.QuoteMessage.Data
import com.encodeering.luminous.application.internal.partner.integration.QuoteMessage.Type.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * @author clausen - encodeering@gmail.com
 */
internal class QuoteMessageTest {

    @ParameterizedTest
    @ValueSource (strings = ["QUOTE"])
    fun `message should be readable` (type: String) {
        assertThat (Json.decodeFromString<QuoteMessage> (json (type))).isEqualTo (QuoteMessage (Data ("XB506502X280", 582.9387), valueOf (type)))
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    @ValueSource (strings = ["OTHER"])
    fun `message should be no readable for other types` (type: String?) {
        assertThrows<SerializationException> { Json.decodeFromString<InstrumentMessage> (json (type)) }
    }

    private fun json (type: String?) = """
    {
      "data": {
        "price": 582.9387,
        "isin": "XB506502X280"
      },
      "type": "$type"
    }
    """.trimIndent ()

}
