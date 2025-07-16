package org.example.project

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
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
        assertEquals(status, shipment.getStatus())
        assertEquals(createdTimestamp, shipment.getCreatedAt())
        assertTrue(shipment.getUpdates().isEmpty())
        assertTrue(shipment.getNotes().isEmpty())
        assertNull(shipment.getCurrentLocation())
    }

    @Test
    fun updateStatusChangesStatus() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)

        // Act
        shipment.updateStatus(ShipmentStatus.SHIPPED)

        // Assert
        assertEquals(ShipmentStatus.SHIPPED, shipment.getStatus())
    }

    @Test
    fun setCurrentLocationUpdatesLocation() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)

        // Act
        shipment.setCurrentLocation("New York")

        // Assert
        assertEquals("New York", shipment.getCurrentLocation())
    }

    @Test
    fun setExpectedDeliveryDateUpdatesDeliveryDate() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        val deliveryDate = 1699209856789L

        // Act
        shipment.setExpectedDeliveryDate(deliveryDate)

        // Assert
        assertEquals(deliveryDate, shipment.getExpectedDeliveryDate())
    }

    @Test
    fun addUpdateAddsUpdateToList() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        val update = ShippingUpdate("CREATED", "SHIPPED", 1699123456789L)

        // Act
        shipment.addUpdate(update)

        // Assert
        assertEquals(1, shipment.getUpdates().size)
        assertEquals(update, shipment.getUpdates()[0])
    }

    @Test
    fun addNoteAddsNoteToList() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        val note = "Package delayed due to weather"

        // Act
        shipment.addNote(note)

        // Assert
        assertEquals(1, shipment.getNotes().size)
        assertEquals(note, shipment.getNotes()[0])
    }

    @Test
    fun copyCreatesNewInstanceWithSameProperties() {
        // Arrange
        val originalShipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        originalShipment.updateStatus(ShipmentStatus.SHIPPED)
        originalShipment.setCurrentLocation("New York")
        originalShipment.addNote("Test note")

        // Act
        val copiedShipment = originalShipment.copy()

        // Assert
        assertEquals(originalShipment.getId(), copiedShipment.getId())
        assertEquals(originalShipment.getStatus(), copiedShipment.getStatus())
        assertEquals(originalShipment.getCreatedAt(), copiedShipment.getCreatedAt())
        assertEquals(originalShipment.getCurrentLocation(), copiedShipment.getCurrentLocation())
        assertEquals(originalShipment.getNotes(), copiedShipment.getNotes())
        assertEquals(originalShipment.getUpdates(), copiedShipment.getUpdates())
    }

    @Test
    fun getFormattedDeliveryDateReturnsFormattedDate() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 1699123456789L)
        shipment.setExpectedDeliveryDate(1699209856789L) // Nov 05, 2023

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