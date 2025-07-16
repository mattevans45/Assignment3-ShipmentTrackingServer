package org.example.project

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ShippingUpdateTest {

    @Test
    fun constructorSetsAllProperties() {
        // Arrange
        val previousStatus = "CREATED"
        val newStatus = "SHIPPED"
        val timestamp = 1699123456789L
        val location = "New York"
        val notes = "Package shipped successfully"

        // Act
        val shippingUpdate = ShippingUpdate(previousStatus, newStatus, timestamp, location, notes)

        // Assert
        assertEquals(previousStatus, shippingUpdate.getPreviousStatus())
        assertEquals(newStatus, shippingUpdate.getNewStatus())
        assertEquals(timestamp, shippingUpdate.getTimestamp())
        assertEquals(location, shippingUpdate.getLocation())
        assertEquals(notes, shippingUpdate.getNotes())
    }

    @Test
    fun constructorHandlesNullLocationAndNotes() {
        // Arrange
        val previousStatus = "CREATED"
        val newStatus = "SHIPPED"
        val timestamp = 1699123456789L

        // Act
        val shippingUpdate = ShippingUpdate(previousStatus, newStatus, timestamp)

        // Assert
        assertEquals(previousStatus, shippingUpdate.getPreviousStatus())
        assertEquals(newStatus, shippingUpdate.getNewStatus())
        assertEquals(timestamp, shippingUpdate.getTimestamp())
        assertNull(shippingUpdate.getLocation())
        assertNull(shippingUpdate.getNotes())
    }

    @Test
    fun getFormattedTimestampReturnsCorrectFormat() {
        // Arrange
        val timestamp = 1699123456789L // Nov 04, 2023 11:44
        val shippingUpdate = ShippingUpdate("CREATED", "SHIPPED", timestamp)

        // Act
        val formattedTimestamp = shippingUpdate.getFormattedTimestamp()

        // Assert
        assertEquals("Nov 04, 2023 11:44", formattedTimestamp)
    }
}