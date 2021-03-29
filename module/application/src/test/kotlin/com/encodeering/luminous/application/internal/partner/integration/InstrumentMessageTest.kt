package com.encodeering.luminous.application.internal.partner.integration

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
internal class InstrumentMessageTest {

    @ParameterizedTest
    @ValueSource (strings = ["ADD", "DELETE"])
    fun `message should be readable` (type: String) {
        assertThat (Json.decodeFromString<InstrumentMessage> (json (type))).isEqualTo (InstrumentMessage (InstrumentMessage.Data ("VE1506683Q53", "convallis quis convenire malorum arcu dolore"), InstrumentMessage.Type.valueOf (type)))
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
            "description": "convallis quis convenire malorum arcu dolore",
            "isin": "VE1506683Q53"
          },
          "type": "$type"
        }
    """.trimIndent ()

}
