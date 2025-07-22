package org.example.project

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull

class ShipmentTest {

    @Test
    fun constructorSetsAllProperties() {
        // Arrange
        val id = "123"
        val status = ShipmentStatus.CREATED
        val createdTimestamp = 1699123456789L

        // Act
        val shipment = Shipment(id, status, createdTimestamp)

        // Assert
        assertEquals(id, shipment.getId())
        assertEquals(status, shipment.status)
        assertEquals(createdTimestamp, shipment.createdTimestamp)
        assertTrue(shipment.updateHistory.isEmpty())
        assertTrue(shipment.notesList.isEmpty())
        assertNull(shipment.currentLocation)
    }

    @Test
    fun updateStatusChangesStatus() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)

        // Act
        shipment.updateStatus(ShipmentStatus.SHIPPED)

        // Assert
        assertEquals(ShipmentStatus.SHIPPED, shipment.status)
    }

    @Test
    fun setCurrentLocationUpdatesLocation() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)

        // Act
        shipment.updateCurrentLocation("New York")

        // Assert
        assertEquals("New York", shipment.currentLocation)
    }

    @Test
    fun setExpectedDeliveryDateUpdatesDeliveryDate() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        val deliveryDate = 1699209856789L

        // Act
        shipment.updateExpectedDeliveryDateTimestamp(deliveryDate)

        // Assert
        assertEquals(deliveryDate, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun addUpdateAddsUpdateToList() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        val update = ShippingUpdate("CREATED", "SHIPPED", 1699123456789L)

        // Act
        shipment.addUpdate(update)

        // Assert
        assertEquals(1, shipment.updateHistory.size)
        assertEquals(update, shipment.updateHistory[0])
    }

    @Test
    fun addNoteAddsNoteToList() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        val note = "Package delayed due to weather"

        // Act
        shipment.addNote(note)

        // Assert
        assertEquals(1, shipment.notesList.size)
        assertEquals(note, shipment.notesList[0])
    }


    @Test
    fun getFormattedDeliveryDateReturnsFormattedDate() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        shipment.updateExpectedDeliveryDateTimestamp(1699209856789L) // Nov 05, 2023

        // Act
        val formattedDate = shipment.getFormattedDeliveryDate()

        // Assert
        assertEquals("Nov 05, 2023 11:44", formattedDate)
    }

    @Test
    fun getFormattedDeliveryDateReturnsUnknownWhenNoDeliveryDate() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)

        // Act
        val formattedDate = shipment.getFormattedDeliveryDate()

        // Assert
        assertEquals("Unknown", formattedDate)
    }
}