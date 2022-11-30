package com.encodeering.luminous.system.partner.internal.ktor

import io.ktor.events.Events
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping

/**
 * @author clausen - encodeering@gmail.com
 */

internal fun Application.events (f: Events.() -> Unit) = f (environment.monitor)

internal fun Events.started      (f: Runnable) { subscribe (ApplicationStarted)       { f.run () } }
internal fun Events.decommission (f: Runnable) { subscribe (ApplicationStopPreparing) { f.run () } }
internal fun Events.stopping     (f: Runnable) { subscribe (ApplicationStopping)      { f.run () } }
internal fun Events.stopped      (f: Runnable) { subscribe (ApplicationStopped)       { f.run () } }
