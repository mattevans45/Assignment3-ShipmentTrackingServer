package org.example.project

interface Subject {
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun notifyObservers(shipment: Shipment)
    fun notifyShipmentCreated(shipment: Shipment)
    fun notifyShipmentNotFound(shipmentId: String)
}