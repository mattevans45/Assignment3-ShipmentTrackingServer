package org.example.project.strategy

import org.example.project.model.*
import org.example.project.server.TrackingServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NoteAddedStrategyTest {
    private lateinit var strategy: NoteAddedStrategy

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        strategy = NoteAddedStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun executeAddsNoteWhenShipmentExists() {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.SHIPPED,
            createdTimestamp = 456L,
            expectedDeliveryDate = 456L + 86400000,
            currentLocation = "In Transit",
            updateHistory = mutableListOf(),
            notes = mutableListOf()
        )
        TrackingServer.addShipment(shipment)
        val note = "Package delayed due to weather"
        val updateData = UpdateData("NOTEADDED", "123", 456L, note)

        // Act
        strategy.execute(updateData)

        // Assert
        val updatedShipment = TrackingServer.getShipment("123")
        assertNotNull(updatedShipment)
        assertEquals(note, updatedShipment.notes.lastOrNull())
    }

    @Test
    fun executeHandlesMissingShipment() {
        // Arrange
        val note = "Package delayed due to weather"
        val updateData = UpdateData("NOTEADDED", "123", 456L, note)

        // Ensure shipment doesn't exist
        assertEquals(null, TrackingServer.getShipment("123"))

        // Act
        strategy.execute(updateData)

        // Assert - shipment should still not exist since NoteAddedStrategy doesn't create shipments
        assertEquals(null, TrackingServer.getShipment("123"))
    }
}