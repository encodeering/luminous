package com.encodeering.luminous.system.partner.internal

import com.encodeering.luminous.system.partner.internal.ktor.Module
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

/**
 * @author clausen - encodeering@gmail.com
 */
internal fun main (args: Array<String>) = EngineMain.main (args)

internal fun Application.module () = ApplicationModule (this).load ()

internal class ApplicationModule (application: Application): Module {

    private fun all () = listOf<Module> ()

    override fun load () {
        all ().forEach { it.load () }
    }

}
