package no.nav.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import no.nav.ApplicationState
import no.nav.metrics.monitorHttpRequests
import no.nav.plugins.nais.isalive.naisIsAliveRoute
import no.nav.plugins.nais.isready.naisIsReadyRoute
import no.nav.plugins.nais.prometheus.naisPrometheusRoute

fun Application.configureRouting(applicationState: ApplicationState) {
    routing {
        naisIsAliveRoute(applicationState)
        naisIsReadyRoute(applicationState)
        naisPrometheusRoute()
    }
    intercept(ApplicationCallPipeline.Monitoring, monitorHttpRequests())
}
