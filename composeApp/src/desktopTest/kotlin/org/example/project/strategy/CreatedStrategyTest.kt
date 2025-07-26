package org.example.project.strategy

import org.example.project.model.*
import org.example.project.server.TrackingServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CreatedStrategyTest {
    private lateinit var strategy: CreatedStrategy

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        strategy = CreatedStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun executeCreatesNewShipmentWhenNotExists() {
        // Arrange
        val updateData = UpdateData("CREATED", "123", 456L, "STANDARD")
        
        // Ensure shipment doesn't exist
        assertNull(TrackingServer.getShipment("123"))

        // Act
        strategy.execute(updateData)

        // Assert
        val createdShipment = TrackingServer.getShipment("123")
        assertNotNull(createdShipment)
        assertEquals(ShipmentStatus.CREATED, createdShipment.status)
        assertEquals("123", createdShipment.id)
    }

    @Test
    fun executeDoesNotCreateShipmentWhenExists() {
        // Arrange
        val existingShipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.SHIPPED,
            createdTimestamp = 456L,
            expectedDeliveryDate = 456L + 86400000,
            currentLocation = "In Transit",
            updateHistory = mutableListOf(),
            notes = mutableListOf()
        )
        TrackingServer.addShipment(existingShipment)
        val updateData = UpdateData("CREATED", "123", 456L, "STANDARD")

        // Act
        strategy.execute(updateData)

        // Assert
        val shipment = TrackingServer.getShipment("123")
        assertNotNull(shipment)
        // Status should remain unchanged since shipment already existed
        assertEquals(ShipmentStatus.SHIPPED, shipment.status)
    }
}