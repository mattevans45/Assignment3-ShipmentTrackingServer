package org.example.project

interface Subject {
    fun addObserver(observer: Observer)
    fun removeObserver(observer: Observer)
    fun notifyObservers()
    fun notifyShipmentCreated()
    fun notifyShipmentNotFound(shipmentId: String)
}