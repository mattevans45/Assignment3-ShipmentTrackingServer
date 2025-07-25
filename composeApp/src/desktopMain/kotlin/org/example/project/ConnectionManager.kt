package org.example.project

import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.util.concurrent.ConcurrentHashMap


object ConnectionManager {
    val connections = ConcurrentHashMap<WebSocketSession, MutableSet<String>>()
    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Shipment::class) {
                subclass(StandardShipment::class); subclass(ExpressShipment::class); subclass(OvernightShipment::class); subclass(BulkShipment::class)
            }
        }
        classDiscriminator = "shipment_type"
    }

    fun onJoin(session: WebSocketSession) {
        connections[session] = ConcurrentHashMap.newKeySet()
        println("New client connected: ${session.hashCode()}")
    }

    fun onLeave(session: WebSocketSession) {
        connections.remove(session)
        println("Client disconnected: ${session.hashCode()}")
    }

    fun subscribe(session: WebSocketSession, shipmentId: String) {
        connections[session]?.add(shipmentId)
        println("Client ${session.hashCode()} is now tracking $shipmentId")
    }

    fun unsubscribe(session: WebSocketSession, shipmentId: String) {
        connections[session]?.remove(shipmentId)
        println("Client ${session.hashCode()} stopped tracking $shipmentId")
    }

    suspend fun notifyShipmentUpdated(shipment: Shipment) {
        val shipmentJson = json.encodeToString(shipment)
        connections.forEach { (session, trackedIds) ->
            if (trackedIds.contains(shipment.id)) {
                session.send(Frame.Text(shipmentJson))
            }
        }
    }
}
