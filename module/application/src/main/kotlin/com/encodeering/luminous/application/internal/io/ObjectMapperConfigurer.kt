@file:JvmName ("ObjectMapperConfigurer")
package com.encodeering.luminous.application.internal.io

import com.fasterxml.jackson.databind.DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.quarkus.jackson.ObjectMapperCustomizer
import io.quarkus.runtime.StartupEvent
import io.vertx.core.json.jackson.DatabindCodec
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

/**
 * @author clausen - encodeering@gmail.com
 */
@ApplicationScoped
internal class ObjectMapperConfigurerVertx {

    @Suppress ("UNUSED_PARAMETER")
    fun customize (@Observes sv: StartupEvent) = DatabindCodec.mapper ().customize ()

}

@ApplicationScoped
internal class ObjectMapperConfigurerQuarkus: ObjectMapperCustomizer {

    override fun customize (objectMapper: ObjectMapper) = objectMapper.customize ()

}

internal fun ObjectMapper.customize () {
    disable (WRITE_DATES_AS_TIMESTAMPS)
    disable (WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
    disable (READ_DATE_TIMESTAMPS_AS_NANOSECONDS)

    registerModule (JavaTimeModule ())
}
