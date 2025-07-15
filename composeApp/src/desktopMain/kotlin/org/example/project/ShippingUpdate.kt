package org.example.project

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ShippingUpdate(
    private val previousStatus: String,
    private val newStatus: String,
    private val timestamp: Long,
    private val location: String? = null,
    private val notes: String? = null
) {
    fun getPreviousStatus(): String = previousStatus
    fun getNewStatus(): String = newStatus
    fun getTimestamp(): Long = timestamp
    fun getLocation(): String? = location
    fun getNotes(): String? = notes

    fun getFormattedTimestamp(): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        return instant.atZone(ZoneId.systemDefault()).format(formatter)
    }
}