package org.example.project.model

interface Observer {
    fun onShipmentUpdated(shipment: Shipment)
    fun onShipmentCreated(shipment: Shipment)
    fun onShipmentNotFound(shipmentId: String)
}