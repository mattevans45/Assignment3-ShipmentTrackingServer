package org.example.project

interface Observer {
    fun onShipmentUpdated(shipment: Shipment)
    fun onShipmentCreated(shipment: Shipment)
    fun onShipmentNotFound(shipmentId: String)
}