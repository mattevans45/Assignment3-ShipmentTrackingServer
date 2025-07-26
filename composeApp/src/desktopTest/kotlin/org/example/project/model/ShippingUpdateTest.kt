package org.example.project.model

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
        assertEquals(previousStatus, shippingUpdate.previousStatus)
        assertEquals(newStatus, shippingUpdate.newStatus)
        assertEquals(timestamp, shippingUpdate.timestamp)
        assertEquals(location, shippingUpdate.location)
        assertEquals(notes, shippingUpdate.notes)
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
        assertEquals(previousStatus, shippingUpdate.previousStatus)
        assertEquals(newStatus, shippingUpdate.newStatus)
        assertEquals(timestamp, shippingUpdate.timestamp)
        assertNull(shippingUpdate.location)
        assertNull(shippingUpdate.notes)
    }

    @Test
    fun getFormattedTimestampReturnsCorrectFormat() {
        // Arrange
        val timestamp = 1699123456789L // Nov 04, 2023 11:44
        val shippingUpdate = ShippingUpdate("CREATED", "SHIPPED", timestamp)

        // Act
        val formattedTimestamp = shippingUpdate.getFormattedTimestamp()

        // Assert
        assertEquals("2023-11-04T11:44:16.789", formattedTimestamp)
    }
}