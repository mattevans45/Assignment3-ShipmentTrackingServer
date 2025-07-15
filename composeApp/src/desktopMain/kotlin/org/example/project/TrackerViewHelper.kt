package org.example.project

import androidx.compose.runtime.*

class TrackerViewHelper : Observer {
    // Track which shipments we're monitoring (as per UML)
    private val trackedShipments: MutableSet<String> = mutableSetOf()
    
  
    private val _trackedShipmentData = mutableStateOf<Map<String, Shipment>>(emptyMap())
    val trackedShipmentData: State<Map<String, Shipment>> = _trackedShipmentData
    
    // Track individual shipment (called by UI)
    fun trackShipment(id: String) {
        println("DEBUG: TrackerViewHelper - Tracking shipment: $id")
        trackedShipments.add(id)
        
        // Get current shipment state from simulator
        val simulator = TrackingSimulator.getInstance()
        val shipment = simulator.getShipment(id)
        
        if (shipment != null) {
            // Simply update the map - Compose will detect the change
            _trackedShipmentData.value = _trackedShipmentData.value + (id to shipment)
        } else {
            println("DEBUG: TrackerViewHelper - Shipment not found: $id")
        }
    }
    
    // Stop tracking shipment (called by UI)
    fun stopTracking(id: String) {
        println("DEBUG: TrackerViewHelper - Stopping tracking: $id")
        trackedShipments.remove(id)
        
        // Remove from tracked shipment data
        _trackedShipmentData.value = _trackedShipmentData.value - id
        
        println("DEBUG: TrackerViewHelper - Now tracking ${trackedShipments.size} shipments")
    }
    
//    // Check if currently tracking a shipment
//    fun isCurrentlyTracking(id: String): Boolean = trackedShipments.contains(id)
    
    // Observer interface implementations - SIMPLE AND CLEAN
    override fun onShipmentUpdated(shipment: Shipment) {
        println("DEBUG: TrackerViewHelper - Shipment updated: ${shipment.getId()}, status: ${shipment.getStatus()}")
        
        // Only update if we're tracking this shipment
        if (trackedShipments.contains(shipment.getId())) {
            // Create a NEW MAP with the updated shipment to trigger recomposition
            _trackedShipmentData.value = _trackedShipmentData.value.toMutableMap().apply {
                put(shipment.getId(), shipment)
            }
            
            println("DEBUG: TrackerViewHelper - State updated, new map size: ${_trackedShipmentData.value.size}")
        }
    }
    
    override fun onShipmentCreated(shipment: Shipment) {
        println("DEBUG: TrackerViewHelper - Shipment created: ${shipment.getId()}")
        
        // Only update if we're tracking this shipment
        if (trackedShipments.contains(shipment.getId())) {
            _trackedShipmentData.value = _trackedShipmentData.value + (shipment.getId() to shipment)
        }
    }
    
    override fun onShipmentNotFound(shipmentId: String) {
        println("DEBUG: TrackerViewHelper - Shipment not found: $shipmentId")
        

        stopTracking(shipmentId)
    }
    
    // Utility methods
    fun resetSimulation() {
        println("DEBUG: TrackerViewHelper - Resetting simulation")
        trackedShipments.clear()
        _trackedShipmentData.value = emptyMap()
    }

}
