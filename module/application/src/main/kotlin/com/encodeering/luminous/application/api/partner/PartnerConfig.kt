package com.encodeering.luminous.application.api.partner

import io.quarkus.arc.config.ConfigProperties
import java.net.URI

/**
 * @author clausen - encodeering@gmail.com
 */
@ConfigProperties (prefix = "partner")
interface PartnerConfig {

    val endpoint: URI

}
