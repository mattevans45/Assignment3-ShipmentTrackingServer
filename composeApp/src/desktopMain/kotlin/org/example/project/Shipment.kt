package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Shipment(
    private val id: String,
    status: ShipmentStatus,
    createdTimestamp: Long,
    expectedDeliveryDateTimestamp: Long? = null,
    currentLocation: String? = null,
    notesList: List<String> = emptyList()
) : Subject {
    private val observers: MutableList<Observer> = mutableListOf()


    var status by mutableStateOf(status)
        private set
    var createdTimestamp by mutableStateOf(createdTimestamp)
        private set
    var expectedDeliveryDateTimestamp by mutableStateOf(expectedDeliveryDateTimestamp)
        private set
    var currentLocation by mutableStateOf(currentLocation)
        private set
    var notesList by mutableStateOf(notesList.toMutableList())
        private set
    var updateHistory by mutableStateOf(mutableListOf<ShippingUpdate>())
        private set

    // Subject interface implementation (unchanged)
    override fun addObserver(observer: Observer) { observers.add(observer) }
    override fun removeObserver(observer: Observer) { observers.remove(observer) }
    override fun notifyObservers() {
        observers.forEach { observer -> observer.onShipmentUpdated(this) }
    }
    override fun notifyShipmentCreated() {
        observers.forEach { observer -> observer.onShipmentCreated(this) }
    }
    override fun notifyShipmentNotFound(shipmentId: String) {
        observers.forEach { observer -> observer.onShipmentNotFound(shipmentId) }
    }

    fun getId(): String = id

    fun updateStatus(newStatus: ShipmentStatus) {
        if (status != newStatus) {
            status = newStatus
            notifyObservers()
        }
    }
    fun updateExpectedDeliveryDateTimestamp(timestamp: Long?) {
        if (expectedDeliveryDateTimestamp != timestamp) {
            expectedDeliveryDateTimestamp = timestamp
            notifyObservers()
        }
    }
    fun updateCurrentLocation(location: String?) {
        if (currentLocation != location) {
            currentLocation = location
            notifyObservers()
        }
    }
    fun addNote(note: String) {
        notesList = (notesList + note) as MutableList<String>
        notifyObservers()
    }
    fun addUpdate(update: ShippingUpdate) {
        updateHistory = (updateHistory + update).toMutableList()
        notifyObservers()
    }

    fun getFormattedDeliveryDate(): String {
        return if (expectedDeliveryDateTimestamp == null || expectedDeliveryDateTimestamp!! <= 0) {
            "Unknown"
        } else {
            val instant = Instant.ofEpochMilli(expectedDeliveryDateTimestamp!!)
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
            instant.atZone(ZoneId.systemDefault()).format(formatter)
        }
    }
}