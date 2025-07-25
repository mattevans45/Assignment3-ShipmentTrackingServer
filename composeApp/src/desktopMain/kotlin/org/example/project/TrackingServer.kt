package org.example.project


import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException



fun Application.shipmentModule() {
    TrackingServer.clearAllShipments()
    TrackingServer.initialize(this)
    val updateProcessor = UpdateProcessor()

    install(WebSockets)
    install(ContentNegotiation) {
        json(ConnectionManager.json)
    }

    routing {
        shipmentRoutes(updateProcessor)
        webSocket("/track") {
            ConnectionManager.onJoin(this)
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText().split(",", limit = 2)
                        val command = text.getOrNull(0)
                        val shipmentId = text.getOrNull(1)
                        if (command != null && shipmentId != null) {
                            when (command) {
                                "track" -> ConnectionManager.subscribe(this, shipmentId)
                                "untrack" -> ConnectionManager.unsubscribe(this, shipmentId)
                            }
                        }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                println("Connection closed normally.")
            } catch (e: Exception) {
                println("Error in WebSocket session: ${e.localizedMessage}")
            } finally {
                ConnectionManager.onLeave(this)
            }
        }
    }
}



fun Route.shipmentRoutes(updateProcessor: UpdateProcessor) {
    route("/shipments") {
        get { call.respond(TrackingServer.getAllShipments()) }
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            TrackingServer.getShipment(id)?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound)
        }
        post {
            try {
                val payload = call.receive<ShipmentUpdatePayload>()
                updateProcessor.process(payload)
                call.respond(HttpStatusCode.Created)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request: ${e.message}")
            }
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (TrackingServer.remove(id)) call.respond(HttpStatusCode.NoContent) else call.respond(HttpStatusCode.NotFound)
        }
    }
}