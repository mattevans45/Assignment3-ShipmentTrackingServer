package org.example.project

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*


fun Route.shipmentRoutes() {
    route("/shipments") {
        get {
            val shipments = TrackingSimulator.getInstance().getAllShipments()
            call.respond(shipments.values.toList())
        }

        post {
            val shipment = call.receive<Shipment>()
            TrackingSimulator.getInstance().addShipment(shipment)
            call.respond(HttpStatusCode.Created, shipment)
        }

        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing or malformed id")
            val shipment = call.receive<Shipment>()
            if (shipment.getId() != id) {
                return@put call.respond(HttpStatusCode.BadRequest, "Shipment ID mismatch")
            }
            TrackingSimulator.getInstance().updateShipment(shipment)
            call.respond(HttpStatusCode.OK, shipment)
        }

        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing or malformed id")
            TrackingSimulator.getInstance().clearAllShipments()
            call.respond(HttpStatusCode.NoContent)
        }
    }
}