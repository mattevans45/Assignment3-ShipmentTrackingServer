package org.example.project

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Shipment(
    private val id: String,
    private var status: ShipmentStatus,
    private val createdTimestamp: Long,
    private var expectedDeliveryDateTimestamp: Long? = null,
    private var currentLocation: String? = null,
    private val notesList: MutableList<String> = mutableListOf()
) {
    private val updates: MutableList<ShippingUpdate> = mutableListOf()


    fun getId(): String = id
    fun getStatus(): ShipmentStatus = status
    fun getCreatedAt(): Long = createdTimestamp
    fun getExpectedDeliveryDate(): Long? = expectedDeliveryDateTimestamp
    fun getCurrentLocation(): String? = currentLocation
    fun getNotes(): List<String> = notesList.toList()
    fun getUpdates(): List<ShippingUpdate> = updates.toList()

    fun updateStatus(newStatus: ShipmentStatus) {
        if (status != newStatus) {
            status = newStatus
        }
    }

    fun setExpectedDeliveryDate(date: Long) {
        if (expectedDeliveryDateTimestamp != date) {
            expectedDeliveryDateTimestamp = date
        }
    }

    fun setCurrentLocation(location: String?) {
        if (currentLocation != location) {
            currentLocation = location
        }
    }

    fun addNote(note: String) {
        notesList.add(note)
    }

    fun addUpdate(update: ShippingUpdate) {
        updates.add(update)
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

    fun getFormattedUpdateHistory(): List<String> {
        return updates.map { update ->
            "${formatTimestamp(update.getTimestamp())}: ${update.getPreviousStatus()} → ${update.getNewStatus()}"
        }
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    // Add this method to your Shipment class
    fun copy(): Shipment {
        val newShipment = Shipment(
            id = this.id,
            status = this.status,
            createdTimestamp = this.createdTimestamp,
            expectedDeliveryDateTimestamp = this.expectedDeliveryDateTimestamp
        )
        
        // Copy current location
        newShipment.setCurrentLocation(this.getCurrentLocation())
        
        // Copy all notes
        this.getNotes().forEach { newShipment.addNote(it) }
        
        // Copy all updates
        this.getUpdates().forEach { newShipment.addUpdate(it) }
        
        return newShipment
    }

}