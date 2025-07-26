package org.example.project.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
data class ShippingUpdate(
    val previousStatus: String = "",
    val newStatus: String = "",
    val timestamp: Long = 0L,
    val location: String? = null,
    val notes: String? = null
) {
    fun getFormattedTimestamp(): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    override fun toString(): String {
        return "ShippingUpdate(previousStatus='$previousStatus', newStatus='$newStatus', timestamp=$timestamp, location=$location, notes=$notes)"
    }
}