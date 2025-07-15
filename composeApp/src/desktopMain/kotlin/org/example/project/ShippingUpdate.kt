
package org.example.project

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
}