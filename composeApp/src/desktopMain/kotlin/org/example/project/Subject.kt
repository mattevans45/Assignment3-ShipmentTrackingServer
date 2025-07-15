package org.example.project

interface Subject {
    suspend fun addObserver(observer: Observer)
    suspend fun removeObserver(observer: Observer)
    fun notifyObservers(shipment: Shipment)
    fun notifyShipmentNotFound(shipmentId: String)
    fun notifyShipmentCreated(shipment: Shipment)
}