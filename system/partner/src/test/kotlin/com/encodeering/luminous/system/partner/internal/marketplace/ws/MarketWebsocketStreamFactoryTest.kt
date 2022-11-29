package com.encodeering.luminous.system.partner.internal.marketplace.ws

import com.encodeering.luminous.system.partner.api.marketplace.Marketplace
import com.encodeering.luminous.system.partner.internal.marketplace.Config
import com.encodeering.luminous.system.partner.internal.marketplace.MarketplaceDefault
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory.Companion.Key
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory.Companion.Key.INSTRUMENT
import com.encodeering.luminous.system.partner.internal.marketplace.ws.MarketWebsocketStreamFactory.Companion.Key.QUOTE
import com.encodeering.luminous.system.partner.testing.io.RecordingMessenger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.Clock

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MarketWebsocketStreamFactoryTest {

    @ParameterizedTest
    @EnumSource (Key::class)
    fun `create should provide a market websocket stream` (key: Key) {
        assertThat (factory ().create (key, messenger ())).isInstanceOf (MarketWebsocketStream::class.java)
    }

    @Test
    fun `create should activate dump for instrument data` () {
        assertThat ((factory ().create (INSTRUMENT, messenger ()) as MarketWebsocketStream).dump).isTrue ()
    }

    @Test
    fun `create should deactivate dump for quote data` () {
        assertThat ((factory ().create (QUOTE, messenger ()) as MarketWebsocketStream).dump).isFalse ()
    }

    // data driven test part of an integration test

    private companion object {

        fun messenger () = RecordingMessenger<String> (1)

        fun marketplace () = MarketplaceDefault (Clock.systemUTC (), Config ())

        fun factory (marketplace: Marketplace = marketplace ()) = MarketWebsocketStreamFactory (marketplace)

    }

}
