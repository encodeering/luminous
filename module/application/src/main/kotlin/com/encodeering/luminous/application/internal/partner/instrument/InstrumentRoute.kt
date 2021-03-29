package com.encodeering.luminous.application.internal.partner.instrument

import com.encodeering.luminous.application.api.partner.PartnerConfig
import com.encodeering.luminous.application.api.partner.instrument.Instrument
import com.encodeering.luminous.application.api.partner.instrument.InstrumentRepository
import com.encodeering.luminous.application.internal.partner.instrument.InstrumentMessage.Type.ADD
import com.encodeering.luminous.application.internal.partner.instrument.InstrumentMessage.Type.DELETE
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
internal class InstrumentRoute (private val config: PartnerConfig): RouteBuilder () {

    override fun configure () {
        from ("ahc-ws://${config.endpoint}/instruments").routeId ("partner-instruments")
            .transform ()
                .message (this::instrumentify)
            .to ("log:instruments")
    }

    private fun instrumentify                           (message: Message): InstrumentMessage {
        return json.decodeFromString<InstrumentMessage> (message.getBody (String::class.java))
    }

    private companion object {

        val json = Json { ignoreUnknownKeys = true }

    }

}

@ApplicationScoped
internal class InstrumentStorageRoute (private val instruments: InstrumentRepository): RouteBuilder () {

    override fun configure () {
        from ("direct:track-instrument")
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
