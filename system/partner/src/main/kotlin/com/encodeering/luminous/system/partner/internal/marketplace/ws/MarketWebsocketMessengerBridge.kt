package com.encodeering.luminous.system.partner.internal.marketplace.ws

import com.encodeering.luminous.system.partner.api.io.Messenger
import com.encodeering.luminous.system.partner.api.marketplace.Asset
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.CREATE
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.DELETE
import com.encodeering.luminous.system.partner.api.marketplace.Notifier.Code.UPDATE
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal data class MarketMessage (val code: Code, val asset: Asset)

internal class MarketWebsocketMessengerBridge (private val json: Json) {

    fun instrument (messenger: Messenger<String>): Messenger<MarketMessage> = Messenger {
             (code, asset) ->
        when (code) {
            CREATE -> InstrumentMessage (InstrumentMessage.Data (asset.isin, asset.description, asset.stamp.toKotlinInstant ()), InstrumentMessage.Type.ADD)
            DELETE -> InstrumentMessage (InstrumentMessage.Data (asset.isin, asset.description, asset.stamp.toKotlinInstant ()), InstrumentMessage.Type.DELETE)
            else   -> null
        }?.let (json::encodeToString)?.let (messenger::send)
    }

    fun quote (messenger: Messenger<String>): Messenger<MarketMessage> = Messenger {
             (code, asset) ->
        when (code) {
            UPDATE -> QuoteMessage (QuoteMessage.Data (asset.isin, asset.price, asset.stamp.toKotlinInstant ()), QuoteMessage.Type.QUOTE)
            else   -> null
        }?.let (json::encodeToString)?.let (messenger::send)
    }

}

@Serializable
internal data class InstrumentMessage (val data: Data, val type: Type) {

    @Serializable
    data class Data (val isin: String, val description: String, val stamp: Instant)

    @Serializable
    enum class Type { ADD, DELETE }

}

@Serializable
internal data class QuoteMessage (val data: Data, val type: Type) {

    @Serializable
    data class Data (val isin: String, val price: Double, val stamp: Instant)

    @Serializable
    enum class Type { QUOTE }

}
