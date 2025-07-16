package org.example.project

import java.util.concurrent.ConcurrentHashMap

class TrackingSimulator private constructor() : Subject {
    companion object {
        private var instance: TrackingSimulator? = null

        fun getInstance(): TrackingSimulator {
            if (instance == null) {
                instance = TrackingSimulator()
            }
            return instance!!
        }

        @JvmStatic
        fun setTestInstance(testInstance: TrackingSimulator) {
            instance = testInstance
        }

        @JvmStatic
        fun resetInstance() {
            instance = null
        }
    }

    private val shipments: MutableMap<String, Shipment> = ConcurrentHashMap()
    private val observers: MutableList<Observer> = mutableListOf()

    override fun addObserver(observer: Observer) {
        synchronized(observers) {
            observers.add(observer)
        }
    }

    override fun removeObserver(observer: Observer) {
        synchronized(observers) {
            observers.remove(observer)
        }
    }

    fun addShipment(shipment: Shipment) {
        shipments[shipment.getId()] = shipment
        notifyShipmentCreated(shipment)
    }

    fun getShipment(id: String): Shipment? {
        return shipments[id]
    }

    fun updateShipment(shipment: Shipment) {
        shipments[shipment.getId()] = shipment
    }

    fun isShipmentExists(id: String): Boolean = shipments.containsKey(id)


    override fun notifyObservers(shipment: Shipment) {
        synchronized(observers) {
            observers.forEach { observer ->
                try {
                    observer.onShipmentUpdated(shipment)
                } catch (e: Exception) {
                    println("ERROR: Failed to notify observer: ${e.message}")
                }
            }
        }
    }

    override fun notifyShipmentCreated(shipment: Shipment) {
        synchronized(observers) {
            observers.forEach { observer ->
                try {
                    observer.onShipmentCreated(shipment)
                } catch (e: Exception) {
                    println("ERROR: Failed to notify observer: ${e.message}")
                }
            }
        }
    }

    override fun notifyShipmentNotFound(shipmentId: String) {
        synchronized(observers) {
            observers.forEach { observer ->
                try {
                    observer.onShipmentNotFound(shipmentId)
                } catch (e: Exception) {
                    println("ERROR: Failed to notify observer: ${e.message}")
                }
            }
        }
    }

    fun clearAllShipments() {
        synchronized(shipments) {
            println("DEBUG: Clearing all shipments")
            val clearedCount = shipments.size
            shipments.clear()
            println("DEBUG: Cleared $clearedCount shipments")
        }
    }

}