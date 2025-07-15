package org.example.project

interface Observer {
    suspend fun onShipmentUpdated(shipment: Shipment)
    suspend fun onShipmentCreated(shipment: Shipment)
    suspend fun onShipmentNotFound(shipmentId: String)
}