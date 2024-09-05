package no.nav

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.plugins.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

val logger: Logger = LoggerFactory.getLogger("no.nav.dvhflrimport2")


fun main() {
   val embeddedServer =
        embeddedServer(
            Netty,
            port = 8080,
            module = Application::module,
        )
    Runtime.getRuntime()
        .addShutdownHook(
            Thread {
                logger.info("Shutting down application from shutdown hook")
                embeddedServer.stop(TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(10))
            },
        )
    embeddedServer.start(true)
}

fun Application.module() {
    val applicationState = ApplicationState()
    configureRouting(applicationState)
}

data class ApplicationState(
    var alive: Boolean = true,
    var ready: Boolean = true,
)