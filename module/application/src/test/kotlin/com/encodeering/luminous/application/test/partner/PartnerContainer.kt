package com.encodeering.luminous.application.test.partner

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitAllStrategy

/**
 * @author clausen - encodeering@gmail.com
 */
internal class PartnerContainer (alias: String): GenericContainer<PartnerContainer> ("encodeering/luminous-partner:0.0.1-SNAPSHOT") {

    init {
        withNetworkAliases (alias)
        withExposedPorts (8080)
        waitingFor (WaitAllStrategy ()
            .withStrategy (Wait.forLogMessage (".*Application - Responding at.*", 1))
            .withStrategy (Wait.forHttp ("/internal/prometheus"))
        )
    }

}
