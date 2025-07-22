package org.example.project

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap

class TrackerViewHelper : Observer {

    private val trackedShipments: MutableSet<String> = mutableSetOf()
    private val simulator: TrackingSimulator
        get() = TrackingSimulator.getInstance()

    private val _trackedShipmentData = mutableStateMapOf<String, Shipment>()
    val trackedShipmentData: SnapshotStateMap<String, Shipment> = _trackedShipmentData

    fun trackShipment(id: String) {
        println("DEBUG: TrackerViewHelper - Tracking shipment: $id")
        trackedShipments.add(id)

        val shipment = simulator.getShipment(id)
        if (shipment != null) {
            shipment.addObserver(this)
            _trackedShipmentData[id] = shipment
        } else {
            println("DEBUG: TrackerViewHelper - Shipment not found: $id")
        }
    }

    fun stopTracking(id: String) {
        println("DEBUG: TrackerViewHelper - Stopping tracking: $id")
        trackedShipments.remove(id)
        val shipment = simulator.getShipment(id)
        shipment?.removeObserver(this)
        _trackedShipmentData.remove(id)
        println("DEBUG: TrackerViewHelper - Now tracking ${trackedShipments.size} shipments")
    }

    override fun onShipmentUpdated(shipment: Shipment) {
        println("DEBUG: TrackerViewHelper - Shipment updated: ${shipment.getId()}, status: ${shipment.status}")
        if (trackedShipments.contains(shipment.getId())) {
            _trackedShipmentData[shipment.getId()] = shipment
        }
    }

    override fun onShipmentCreated(shipment: Shipment) {
        println("DEBUG: TrackerViewHelper - Shipment created: ${shipment.getId()}")
        if (trackedShipments.contains(shipment.getId())) {
            _trackedShipmentData[shipment.getId()] = shipment
        }
    }

    override fun onShipmentNotFound(shipmentId: String) {
        println("DEBUG: TrackerViewHelper - Shipment not found: $shipmentId")
        stopTracking(shipmentId)
    }

    fun resetSimulation() {
        println("DEBUG: TrackerViewHelper - Resetting simulation")
        trackedShipments.forEach { id ->
            val shipment = simulator.getShipment(id)
            shipment?.removeObserver(this)
        }
        trackedShipments.clear()
        _trackedShipmentData.clear()
    }
}