package no.nav.plugins

import io.ktor.server.application.*
import no.nav.ApplicationState
import no.nav.logger

fun Application.configureLifecycleHooks(applicationState: ApplicationState) {

    environment.monitor.subscribe(ApplicationStarted) {
        logger.info("Got ApplicationStarted event from ktor")
        applicationState.ready = true
    }
    environment.monitor.subscribe(ApplicationStopped) {
        logger.info("Got ApplicationStopped event from ktor")
        applicationState.ready = false
    }
}
