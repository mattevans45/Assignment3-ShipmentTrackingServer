package org.example.project.strategy

import org.example.project.model.*
import org.example.project.server.TrackingServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LostStrategyTest {
    private lateinit var strategy: LostStrategy

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        strategy = LostStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun executeMarksShipmentAsLostWhenNotDelivered() {
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
        val updateData = UpdateData("LOST", "123", 456L)

        // Act
        strategy.execute(updateData)

        // Assert
        val updatedShipment = TrackingServer.getShipment("123")
        assertNotNull(updatedShipment)
        assertEquals(ShipmentStatus.LOST, updatedShipment.status)
    }



    @Test
    fun executeHandlesMissingShipment() {
        // Arrange
        val updateData = UpdateData("LOST", "123", 456L)

        // Ensure shipment doesn't exist
        assertEquals(null, TrackingServer.getShipment("123"))

        // Act
        strategy.execute(updateData)

        // Assert - shipment should still not exist since LostStrategy doesn't create shipments
        assertEquals(null, TrackingServer.getShipment("123"))
    }
}