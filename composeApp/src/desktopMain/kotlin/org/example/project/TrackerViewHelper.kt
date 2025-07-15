package org.example.project

import androidx.compose.runtime.*
import kotlinx.coroutines.*

class TrackerViewHelper : Observer {
    // UML-specified state properties
    private val _shipmentId = mutableStateOf("")
    val shipmentId: State<String> = _shipmentId

    private val _shipmentStatus = mutableStateOf("")
    val shipmentStatus: State<String> = _shipmentStatus

    private val _shipmentUpdateHistory = mutableStateOf(listOf<String>())
    val shipmentUpdateHistory: State<List<String>> = _shipmentUpdateHistory

    private val _expectedShipmentDeliveryDate = mutableStateOf(listOf<String>())
    val expectedShipmentDeliveryDate: State<List<String>> = _expectedShipmentDeliveryDate

    private val _trackedShipmentsData = mutableStateOf(mapOf<String, Shipment>())
    val trackedShipments: State<Map<String, Shipment>> = _trackedShipmentsData

    // Force UI updates by incrementing a counter
    private val _updateTrigger = mutableIntStateOf(0)
    val updateTrigger: State<Int> = _updateTrigger

    private val trackedShipmentIds = mutableSetOf<String>()
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Observer interface implementations
    override suspend fun onShipmentUpdated(shipment: Shipment) {
        withContext(Dispatchers.Main) {
            println("DEBUG: TrackerViewHelper - Shipment update for: ${shipment.getId()}")
            
            // Only update if we're tracking this shipment
            if (trackedShipmentIds.contains(shipment.getId())) {
                println("DEBUG: Updating UI state for shipment ${shipment.getId()}")

                // ALWAYS force UI update - create completely new map
                val newMap = mutableMapOf<String, Shipment>()
                newMap.putAll(_trackedShipmentsData.value)
                newMap[shipment.getId()] = shipment
                _trackedShipmentsData.value = newMap
                
                // ALWAYS increment trigger to force recomposition
                _updateTrigger.intValue = _updateTrigger.intValue + 1

                // Update individual state if this is the current shipment
                if (_shipmentId.value == shipment.getId() || _shipmentId.value.isEmpty()) {
                    _shipmentId.value = shipment.getId()
                    _shipmentStatus.value = shipment.getStatus().toString()
                    _shipmentUpdateHistory.value = shipment.getFormattedUpdateHistory()
                    _expectedShipmentDeliveryDate.value = listOf(shipment.getFormattedDeliveryDate())
                }
                
                println("DEBUG: UI state updated - Status: ${shipment.getStatus()}, Location: ${shipment.getCurrentLocation()}, Updates: ${shipment.getUpdates().size}, Trigger: ${_updateTrigger.intValue}")
            } else {
                println("DEBUG: Shipment ${shipment.getId()} not being tracked, ignoring update")
            }
        }
    }

    override suspend fun onShipmentCreated(shipment: Shipment) {
        withContext(Dispatchers.Main) {
            println("DEBUG: TrackerViewHelper - New shipment created: ${shipment.getId()}")
            // If we're already tracking this shipment ID, update immediately
            if (trackedShipmentIds.contains(shipment.getId())) {
                onShipmentUpdated(shipment)
            }
        }
    }

    override suspend fun onShipmentNotFound(shipmentId: String) {
        withContext(Dispatchers.Main) {
            println("DEBUG: TrackerViewHelper - Shipment not found: $shipmentId")
            // Remove from tracking if it was being tracked
            stopTracking(shipmentId)
        }
    }

    // Tracking methods
    fun trackShipment(shipmentId: String) {
        uiScope.launch {
            println("DEBUG: Starting to track shipment: $shipmentId")
            trackedShipmentIds.add(shipmentId)

            // Get current shipment state
            val simulator = TrackingSimulator.getInstance()
            val shipment = simulator.getShipment(shipmentId)

            if (shipment != null) {
                println("DEBUG: Found existing shipment $shipmentId with status: ${shipment.getStatus()}")
                
                // Force UI update
                onShipmentUpdated(shipment)
                
                println("DEBUG: UI state updated for ${shipment.getId()}")
            } else {
                println("DEBUG: Shipment $shipmentId not found yet, will update when created")
            }
        }
    }

    suspend fun stopTracking(shipmentId: String) {
        withContext(Dispatchers.Main) {
            println("DEBUG: Stopping tracking for shipment: $shipmentId")
            trackedShipmentIds.remove(shipmentId)

            // Create completely new map without the shipment
            val newMap = mutableMapOf<String, Shipment>()
            newMap.putAll(_trackedShipmentsData.value)
            newMap.remove(shipmentId)
            _trackedShipmentsData.value = newMap
            
            // Force UI update
            _updateTrigger.intValue = _updateTrigger.intValue + 1

            // Clear individual state if this was the current shipment
            if (_shipmentId.value == shipmentId) {
                _shipmentId.value = ""
                _shipmentStatus.value = ""
                _shipmentUpdateHistory.value = emptyList()
                _expectedShipmentDeliveryDate.value = emptyList()
            }
        }
    }

    fun isCurrentlyTracking(shipmentId: String): Boolean {
        return trackedShipmentIds.contains(shipmentId)
    }

    fun resetSimulation() {
        println("DEBUG: TrackerViewHelper - Resetting simulation data")
        
        // Clear all tracking data
        trackedShipmentIds.clear()
        
        // Reset all state variables
        _trackedShipmentsData.value = emptyMap()
        _shipmentId.value = ""
        _shipmentStatus.value = ""
        _shipmentUpdateHistory.value = emptyList()
        _expectedShipmentDeliveryDate.value = emptyList()
        
        // Force UI update
        _updateTrigger.intValue = _updateTrigger.intValue + 1
        
        println("DEBUG: TrackerViewHelper - Simulation data reset complete")
    }

    fun cleanup() {
        println("DEBUG: TrackerViewHelper cleanup called")
        uiScope.cancel()
        resetSimulation()
    }
}
