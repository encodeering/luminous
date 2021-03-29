package com.encodeering.luminous.application.internal.partner.integration

import com.encodeering.luminous.application.api.partner.PartnerConfig
import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import com.encodeering.luminous.application.internal.partner.integration.InstrumentMessage.Type.ADD
import com.encodeering.luminous.application.internal.partner.integration.InstrumentMessage.Type.DELETE
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.camel.Message
import org.apache.camel.builder.RouteBuilder
import javax.enterprise.context.ApplicationScoped

/**
 * @author clausen - encodeering@gmail.com
 */
private const val operation = "operation"

@ApplicationScoped
internal class PartnerRoute (private val config: PartnerConfig): RouteBuilder () {

    override fun configure () {
        from ("ahc-ws://${config.endpoint}/instruments").routeId ("partner-instruments")
            .transform ()
                .message (this::instrumentify)
            .to ("direct:partner-track-record")
    }

    private fun instrumentify                                     (message: Message): Instrument {
        val instrument = json.decodeFromString<InstrumentMessage> (message.getBody (String::class.java))

        message.setHeader (operation, instrument.type)

        return instrument.data.asInstrument ()
    }

    private companion object {

        val json = Json { ignoreUnknownKeys = true }

    }

}

@ApplicationScoped
internal class PartnerStorageRoute (private val instruments: InstrumentRepository): RouteBuilder () {

    override fun configure () {
        from ("direct:partner-track-record")
            .choice ()
                .`when` (header (operation).isEqualTo (ADD)   ).process ().body (Instrument::class.java, instruments::add).endChoice ()
                .`when` (header (operation).isEqualTo (DELETE)).process ().body (Instrument::class.java, instruments::remove).endChoice ()
                .otherwise ().throwException (IllegalStateException ()).endChoice ()
            .end ()
    }

}

@Serializable
internal data class InstrumentMessage (val data: Data, val type: Type) {

    @Serializable
    data class Data (val isin: String, val description: String)

    @Serializable
    enum class Type { ADD, DELETE }

}

internal fun InstrumentMessage.Data.asInstrument (): Instrument = Instrument (isin, description)
