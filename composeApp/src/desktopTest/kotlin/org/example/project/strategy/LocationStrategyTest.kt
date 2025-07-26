package org.example.project.strategy

import org.example.project.model.*
import org.example.project.server.TrackingServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LocationStrategyTest {
    private lateinit var strategy: LocationStrategy

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        strategy = LocationStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun executeUpdatesLocationWhenShipmentExists() {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.SHIPPED,
            createdTimestamp = 456L,
            expectedDeliveryDate = 456L + 86400000,
            currentLocation = "Origin",
            updateHistory = mutableListOf(),
            notes = mutableListOf()
        )
        TrackingServer.addShipment(shipment)
        val updateData = UpdateData("LOCATION", "123", 456L, "New York")

        // Act
        strategy.execute(updateData)

        // Assert
        val updatedShipment = TrackingServer.getShipment("123")
        assertNotNull(updatedShipment)
        assertEquals("New York", updatedShipment.currentLocation)
    }

    @Test
    fun executeHandlesMissingShipment() {
        // Arrange
        val updateData = UpdateData("LOCATION", "123", 456L, "New York")
        
        // Ensure shipment doesn't exist
        assertEquals(null, TrackingServer.getShipment("123"))

        // Act
        strategy.execute(updateData)

        // Assert - shipment should still not exist since LocationStrategy doesn't create shipments
        assertEquals(null, TrackingServer.getShipment("123"))
    }
}