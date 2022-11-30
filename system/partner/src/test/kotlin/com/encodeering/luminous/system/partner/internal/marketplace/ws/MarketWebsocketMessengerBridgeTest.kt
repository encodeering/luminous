package com.encodeering.luminous.system.partner.internal.marketplace.ws

import com.encodeering.luminous.system.partner.api.io.Messenger
import com.encodeering.luminous.system.partner.api.marketplace.Asset
import com.encodeering.luminous.system.partner.api.marketplace.Notifier
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.*
import com.encodeering.luminous.system.partner.testing.playground.Assets
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MarketWebsocketMessengerBridgeTest {

    @ParameterizedTest
    @MethodSource ("instruments")
    fun `messages converter handle instrument data` (code: Notifier.Code, asset: Asset, conversion: String?) {
        val messenger = mock<Messenger<String>> ()

        bridge ().instrument (messenger).send (MarketMessage (code, asset))

        if (conversion != null)
            verify (messenger).send (conversion)
        else
            verifyNoInteractions (messenger)
    }

    @ParameterizedTest
    @MethodSource ("quotes")
    fun `message converter should handle quote data` (code: Notifier.Code, asset: Asset, conversion: String?) {
        val messenger = mock<Messenger<String>> ()

        bridge ().quote (messenger).send (MarketMessage (code, asset))

        if (conversion != null)
            verify (messenger).send (conversion)
        else
            verifyNoInteractions (messenger)
    }

    private companion object {

        val asset = Assets.asset ()

        fun bridge () = MarketWebsocketMessengerBridge (Json)

        @JvmStatic
        fun instruments () = listOf (
            Arguments.of (CREATE, asset, """{"data":{"isin":"FU1212109727","description":"qasf fwegdf","stamp":"2022-11-23T14:46:16.042370100Z"},"type":"ADD"}"""),
            Arguments.of (UPDATE, asset, null),
            Arguments.of (DELETE, asset, """{"data":{"isin":"FU1212109727","description":"qasf fwegdf","stamp":"2022-11-23T14:46:16.042370100Z"},"type":"DELETE"}""")
        )

        @JvmStatic
        fun quotes () = listOf (
            Arguments.of (CREATE, asset, null),
            Arguments.of (UPDATE, asset, """{"data":{"isin":"FU1212109727","price":12.0,"stamp":"2022-11-23T14:46:16.042370100Z"},"type":"QUOTE"}"""),
            Arguments.of (DELETE, asset, null)
        )

    }

}
