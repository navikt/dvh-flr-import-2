package no.nav.plugins


import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import no.nav.ApplicationState
import no.nav.logger
import no.nav.module
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RoutingTest {

    @Test
    internal fun `Returns internal server error when liveness check fails`() {
        testApplication {
            val applicationState = ApplicationState()
            applicationState.ready = false
            applicationState.alive = false

            application {
                configureRouting(applicationState)
            }
            val response = client.get("/internal/is_alive")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals("I'm dead x_x", response.bodyAsText())
        }
    }

    @Test
    internal fun `Returns internal server error when readyness check fails`() {
        testApplication {
            val applicationState = ApplicationState()
            applicationState.ready = false
            applicationState.alive = false
            application {
                configureRouting(applicationState)
            }

            val response = client.get("/internal/is_ready")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals("Please wait! I'm not ready :(", response.bodyAsText())

        }
    }

    @Test
    internal fun `Returns 404 on page that does not exists`() {
        testApplication {
            application {
                module()
            }
            val response = client.get("/missing-page")
            assertEquals(HttpStatusCode.NotFound, response.status)
            assertEquals("404: Page Not Found", response.bodyAsText())
        }
    }

    @Test
    internal fun `Returns 500 exception when unknown error occurrs`() {
        testApplication {
            val applicationState = ApplicationState()
            applicationState.ready = true
            applicationState.alive = true
            application {
                install(StatusPages) {
                    exception<Throwable> { call, cause ->
                        logger.error("Caught exception ${cause.message}")
                        call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Unknown error")
                        applicationState.alive = false
                        applicationState.ready = false
                    }
                }
                routing {
                    get("/interal_error") {
                        throw Exception()
                    }
                }
            }
            val response = client.get("/interal_error")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals("Unknown error", response.bodyAsText())
        }
    }

}