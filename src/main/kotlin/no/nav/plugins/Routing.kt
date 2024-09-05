package no.nav.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.ApplicationState
import no.nav.logger
import no.nav.plugins.nais.isalive.naisIsAliveRoute
import no.nav.plugins.nais.isready.naisIsReadyRoute
import no.nav.plugins.nais.prometheus.naisPrometheusRoute

fun Application.configureRouting(applicationState: ApplicationState) {
    routing {
        naisIsAliveRoute(applicationState)
        naisIsReadyRoute(applicationState)
        naisPrometheusRoute()
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error("Caught exception ${cause.message}")
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Unknown error")
            applicationState.alive = false
            applicationState.ready = false
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "404: Page Not Found", status = status)
        }
    }
}
