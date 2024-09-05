package no.nav

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.plugins.*
import no.nav.utils.EnvironmentVariables
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

val logger: Logger = LoggerFactory.getLogger("no.nav.dvhflrimport2")

fun main() {
   val environmentVariables = EnvironmentVariables()
   val embeddedServer =
        embeddedServer(
            Netty,
            port = environmentVariables.applicationPort,
            module = Application::module,
        )
    embeddedServer.start(true)
}

fun Application.module() {
    val applicationState = ApplicationState()
    configureRouting(applicationState)
    configureStatusPages(applicationState)
    configureLifecycleHooks(applicationState)

    if (applicationState.ready) {
        log.info("Starting cron job")

        log.info("Ending cron job")
        exitProcess(0)
    }
    else {
        log.info("application is not ready yet")
        exitProcess(0)
    }
}

data class ApplicationState(
    var alive: Boolean = true,
    var ready: Boolean = true,
)