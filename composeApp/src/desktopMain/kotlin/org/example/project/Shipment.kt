package org.example.project

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Shipment : Subject {
    abstract val id: String
    abstract var status: ShipmentStatus
    abstract var updateHistory: List<ShippingUpdate>
    abstract var notes: List<String>
    abstract val createdTimestamp: Long
    abstract var expectedDeliveryDate: Long?
    abstract var currentLocation: String?

    @Transient
    private val observers: MutableList<Observer> = mutableListOf()

    override fun addObserver(observer: Observer) { observers.add(observer) }
    override fun removeObserver(observer: Observer) { observers.remove(observer) }
    override fun notifyObservers() { observers.forEach { it.onShipmentUpdated(this) } }
    override fun notifyShipmentCreated() { observers.forEach { it.onShipmentCreated(this) } }
    override fun notifyShipmentNotFound(shipmentId: String) { observers.forEach { it.onShipmentNotFound(shipmentId) } }


    fun addUpdate(update: ShippingUpdate) { updateHistory = updateHistory + update }
    fun addNote(note: String) { notes = notes + note }
    
    abstract fun validateExpectedDelivery(): String?
    abstract fun isViolationRelevant(): Boolean

    open fun recordViolation(message: String) {
        this.status = ShipmentStatus.EXCEPTION
        this.addNote("Violation: $message")
    }
}