package org.example.project

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Shipment(
    private val id: String,
    private var status: ShipmentStatus,
    private val createdAt: Long,
    private var expectedDeliveryDate: Long? = null,
    private var currentLocation: String? = null,
    private val notesList: MutableList<String> = mutableListOf()
) {
    private val updates: MutableList<ShippingUpdate> = mutableListOf()
    private val simulator: TrackingSimulator = TrackingSimulator.getInstance()

    // Getters
    fun getId(): String = id
    fun getStatus(): ShipmentStatus = status
    fun getCreatedAt(): Long = createdAt
    fun getExpectedDeliveryDate(): Long? = expectedDeliveryDate
    fun getCurrentLocation(): String? = currentLocation
    fun getNotes(): String? = if (notesList.isEmpty()) null else notesList.joinToString("\n")
    fun getUpdates(): List<ShippingUpdate> = updates.toList()
    fun getNotesList(): List<String> = notesList.toList()

    // Simple setters - just change data, no update creation
    fun updateStatus(newStatus: ShipmentStatus) {
        if (status != newStatus) {
            status = newStatus
            println("DEBUG: Shipment ${id} status changed to $newStatus")
            notifyChange()
        }
    }

    fun setExpectedDeliveryDate(date: Long) {
        if (expectedDeliveryDate != date) {
            expectedDeliveryDate = date
            println("DEBUG: Shipment ${id} delivery date changed to $date")
            notifyChange()
        }
    }

    fun setCurrentLocation(location: String) {
        if (currentLocation != location) {
            currentLocation = location
            println("DEBUG: Shipment ${id} location changed to $location")
            notifyChange()
        }
    }

    fun addNote(note: String) {
        notesList.add(note)
        println("DEBUG: Shipment ${id} note added: $note")
        notifyChange()
    }

    // Only called from AbstractUpdateStrategy
    fun addUpdate(update: ShippingUpdate) {
        updates.add(update)
        println("DEBUG: Shipment ${id} update added: ${update.getNewStatus()}")
        notifyChange()
    }

    private fun notifyChange() {
        println("DEBUG: Shipment ${id} notifying observers of change")
        simulator.notifyObservers(this)
    }

    // Formatting methods
    fun getFormattedDeliveryDate(): String {
        val deliveryDate = expectedDeliveryDate
        if (deliveryDate == null || deliveryDate == 0L) return "Not set"
        val instant = Instant.ofEpochMilli(deliveryDate)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
}