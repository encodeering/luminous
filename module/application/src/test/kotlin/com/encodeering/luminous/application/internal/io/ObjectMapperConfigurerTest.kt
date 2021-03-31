package com.encodeering.luminous.application.internal.io

import com.fasterxml.jackson.databind.DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
import io.quarkus.runtime.StartupEvent
import io.vertx.core.json.jackson.DatabindCodec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author clausen - encodeering@gmail.com
 */
internal class ObjectMapperConfigurerTest {

    @Test
    fun `customize should configure vertx jacksons objectmapper` () {
        ObjectMapperConfigurerVertx ().customize (StartupEvent ())

        assert (DatabindCodec.mapper ())
    }

    @Test
    fun `customize should configure quarkus jacksons objectmapper` () {
        val mapper = ObjectMapper ()

        ObjectMapperConfigurerQuarkus ().customize (mapper)

        assert (mapper)
    }

    private fun assert (mapper: ObjectMapper) {
        assertThat     (mapper.registeredModuleIds).contains ("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
        assertThat     (mapper.isEnabled (WRITE_DATES_AS_TIMESTAMPS)).isFalse ()
        assertThat     (mapper.isEnabled (WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)).isFalse ()
        assertThat     (mapper.isEnabled (READ_DATE_TIMESTAMPS_AS_NANOSECONDS)).isFalse ()
    }

}
