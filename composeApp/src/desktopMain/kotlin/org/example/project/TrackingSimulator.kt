package org.example.project

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class TrackingSimulator private constructor() : Subject {
    companion object {
        @Volatile
        private var instance: TrackingSimulator? = null

        fun getInstance(): TrackingSimulator {
            return instance ?: synchronized(this) {
                instance ?: TrackingSimulator().also { instance = it }
            }
        }
    }

    // Use thread-safe collections
    private val shipments: MutableMap<String, Shipment> = ConcurrentHashMap()
    private val observers: MutableList<Observer> = mutableListOf()
    private val observerMutex = Mutex()
    private val shipmentsMutex = Mutex()
    
    // Use application scope for notifications
    private val notificationScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Thread-safe observer management
    override suspend fun addObserver(observer: Observer) {
        observerMutex.withLock {
            observers.add(observer)
            println("DEBUG: Observer added from MUTEX added. Total observers: ${observers.size}")
        }
    }

    override suspend fun removeObserver(observer: Observer) {
        observerMutex.withLock {
            observers.remove(observer)
            println("DEBUG: Observer removed. Total observers: ${observers.size}")
        }
    }

    // Add shipment to the system - now used by strategies
    suspend fun addShipment(shipment: Shipment) {
        shipments[shipment.getId()] = shipment
        println("DEBUG: Shipment added: ${shipment.getId()}")
        notifyShipmentCreated(shipment)
    }

    // Find shipment by ID
    fun findShipment(id: String): Shipment? = shipments[id]

    // Get shipment by ID
    fun getShipment(id: String): Shipment? = findShipment(id)

    // Check if shipment exists
    fun isShipmentExists(id: String): Boolean = shipments.containsKey(id)

    // Async notification methods
    override fun notifyObservers(shipment: Shipment) {
        notificationScope.launch {
            observerMutex.withLock {
                observers.toList()
            }.forEach { observer ->
                launch {
                    try {
                        observer.onShipmentUpdated(shipment)
                    } catch (e: Exception) {
                        println("ERROR: Failed to notify observer about shipment ${shipment.getId()}: ${e.message}")
                    }
                }
            }
        }
    }

    override fun notifyShipmentNotFound(shipmentId: String) {
        notificationScope.launch {
            observerMutex.withLock {
                observers.toList()
            }.forEach { observer ->
                launch {
                    try {
                        observer.onShipmentNotFound(shipmentId)
                    } catch (e: Exception) {
                        println("ERROR: Failed to notify observer about shipment not found: ${e.message}")
                    }
                }
            }
        }
    }

    override fun notifyShipmentCreated(shipment: Shipment) {
        notificationScope.launch {
            observerMutex.withLock {
                observers.toList()
            }.forEach { observer ->
                launch {
                    try {
                        observer.onShipmentCreated(shipment)
                    } catch (e: Exception) {
                        println("ERROR: Failed to notify observer about new shipment: ${e.message}")
                    }
                }
            }
        }
    }

    // Debug methods
    fun getAllShipments(): Map<String, Shipment> = shipments.toMap()
    
    fun printStatus() {
        println("DEBUG: TrackingSimulator Status:")
        println("  - Shipments: ${shipments.size}")
        println("  - Observers: ${observers.size}")
        shipments.forEach { (id, shipment) ->
            println("  - Shipment $id: ${shipment.getStatus()}")
        }
    }

    // Cleanup method
    fun cleanup() {
        notificationScope.cancel()
    }

    // Clear all shipments
    suspend fun clearAllShipments() {
        shipmentsMutex.withLock {
            println("DEBUG: TrackingSimulator - Clearing all shipments")
            val clearedCount = shipments.size
            shipments.clear()
            println("DEBUG: TrackingSimulator - Cleared $clearedCount shipments")
        }
    }
}