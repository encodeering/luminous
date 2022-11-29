package com.encodeering.luminous.system.partner.internal.metric

import com.encodeering.luminous.system.partner.testing.ktor.Demos
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author clausen - encodeering@gmail.com
 */
internal class MetricModuleTest {

    @Test
    fun `metric module should expose a prometheus endpoint` () {
        demo {
            val         response = client.get ("/internal/prometheus")
            assertThat (response.status).isEqualTo (HttpStatusCode (200, "OK"))
            assertThat (response.headers["Content-Type"]).isEqualTo ("text/plain; charset=UTF-8")
            assertThat (response.headers["Content-Length"]!!.toInt ()).isGreaterThan (0)
        }
    }

    private fun demo (f: suspend ApplicationTestBuilder.() -> Unit) = Demos.demo {
        application {
            MetricModule (this).load ()
        }

        f ()
    }

}
