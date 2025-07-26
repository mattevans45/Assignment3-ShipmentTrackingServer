package org.example.project.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.model.Shipment
import java.util.concurrent.ConcurrentHashMap



object TrackingServer {
    private val shipments = ConcurrentHashMap<String, Shipment>()
    lateinit var coroutineScope: CoroutineScope

    fun initialize(scope: CoroutineScope) {
        coroutineScope = scope
    }
    fun getAllShipments(): List<Shipment> = shipments.values.toList()
    fun getShipment(id: String): Shipment? = shipments[id]
    fun addShipment(shipment: Shipment) {
        shipments[shipment.id] = shipment
        shipment.notifyShipmentCreated()
        coroutineScope.launch {
            ConnectionManager.notifyShipmentUpdated(shipment)
        }
    }
    fun updateShipment(shipment: Shipment) {
        shipments[shipment.id] = shipment
        coroutineScope.launch {
            ConnectionManager.notifyShipmentUpdated(shipment)
        }
    }
    fun remove(id: String): Boolean = shipments.remove(id) != null
    fun clearAllShipments() = shipments.clear()
}