package org.example.project.strategy

import org.example.project.model.*
import org.example.project.server.TrackingServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DelayedStrategyTest {
    private lateinit var strategy: DelayedStrategy

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        strategy = DelayedStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun executeDelaysShipmentWhenNotDelivered() {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.SHIPPED,
            createdTimestamp = 456L,
            expectedDeliveryDate = 500L,
            currentLocation = "In Transit",
            updateHistory = mutableListOf(),
            notes = mutableListOf()
        )
        TrackingServer.addShipment(shipment)
        val updateData = UpdateData("DELAYED", "123", 456L, "1000")

        // Act
        strategy.execute(updateData)

        // Assert
        val updatedShipment = TrackingServer.getShipment("123")
        assertNotNull(updatedShipment)
        assertEquals(ShipmentStatus.DELAYED, updatedShipment.status)
        assertEquals(1000L, updatedShipment.expectedDeliveryDate)
    }


    @Test
    fun executeHandlesMissingShipment() {
        // Arrange
        val updateData = UpdateData("DELAYED", "123", 456L, "1000")
        
        // Ensure shipment doesn't exist
        assertEquals(null, TrackingServer.getShipment("123"))

        // Act
        strategy.execute(updateData)

        // Assert - shipment should still not exist since DelayedStrategy doesn't create shipments
        assertEquals(null, TrackingServer.getShipment("123"))
    }
}