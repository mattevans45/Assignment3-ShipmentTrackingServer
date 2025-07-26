package org.example.project.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShipmentTest {

    private fun createShipment(
        id: String = "123",
        status: ShipmentStatus = ShipmentStatus.CREATED,
        createdTimestamp: Long = 1699123456789L,
        updateHistory: List<ShippingUpdate> = emptyList(),
        notes: List<String> = emptyList(),
        expectedDeliveryDate: Long? = null,
        currentLocation: String? = null
    ): StandardShipment {
        return StandardShipment(
            id = id,
            status = status,
            createdTimestamp = createdTimestamp,
            updateHistory = updateHistory.toMutableList(),
            notes = notes.toMutableList(),
            expectedDeliveryDate = expectedDeliveryDate,
            currentLocation = currentLocation
        )
    }

    @Test
    fun constructorSetsAllProperties() {
        // Arrange
        val id = "123"
        val status = ShipmentStatus.CREATED
        val createdTimestamp = 1699123456789L

        // Act
        val shipment = createShipment(id, status, createdTimestamp)

        // Assert
        assertEquals(id, shipment.id)
        assertEquals(status, shipment.status)
        assertEquals(createdTimestamp, shipment.createdTimestamp)
        assertTrue(shipment.updateHistory.isEmpty())
        assertTrue(shipment.notes.isEmpty())
        assertNull(shipment.currentLocation)
    }

    @Test
    fun addUpdateAddsUpdateToList() {
        // Arrange
        val shipment = createShipment()
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
        val shipment = createShipment()
        val note = "Package delayed due to weather"

        // Act
        shipment.addNote(note)

        // Assert
        assertEquals(1, shipment.notes.size)
        assertEquals(note, shipment.notes[0])
    }

    @Test
    fun setCurrentLocationUpdatesLocation() {
        // Arrange
        val shipment = createShipment()

        // Act
        shipment.currentLocation = "New York"

        // Assert
        assertEquals("New York", shipment.currentLocation)
    }

    @Test
    fun setExpectedDeliveryDateUpdatesDeliveryDate() {
        // Arrange
        val shipment = createShipment()
        val deliveryDate = 1699209856789L

        // Act
        shipment.expectedDeliveryDate = deliveryDate

        // Assert
        assertEquals(deliveryDate, shipment.expectedDeliveryDate)
    }
}