package org.example.project

import java.util.concurrent.ConcurrentHashMap

object TrackingSimulator {

    private val shipments: MutableMap<String, Shipment> = ConcurrentHashMap()

    fun addShipment(shipment: Shipment) {
        shipments[shipment.getId()] = shipment
    }

    fun getShipment(id: String): Shipment? = shipments[id]

    fun updateShipment(shipment: Shipment) {
        shipments[shipment.getId()] = shipment
    }

    fun clearAllShipments() {
        synchronized(shipments) {
            println("DEBUG: Clearing all shipments")
            val clearedCount = shipments.size
            shipments.clear()
            println("DEBUG: Cleared $clearedCount shipments")
        }
    }

    fun getAllShipments(): Map<String, Shipment> = shipments.toMap()

    @JvmStatic
    fun resetForTesting() {
        clearAllShipments()
    }

    @Volatile
    private var testInstance: TrackingSimulator? = null

    fun setTestInstance(instance: TrackingSimulator?) {
        testInstance = instance
    }

    fun getInstance(): TrackingSimulator = testInstance ?: this
}