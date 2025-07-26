package org.example.project.strategy

import org.example.project.model.ShipmentStatus
import org.example.project.model.StandardShipment
import org.example.project.model.UpdateData
import org.example.project.server.TrackingServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ShippedStrategyTest {
    private lateinit var strategy: ShippedStrategy

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        strategy = ShippedStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }



    @Test
    fun executeDoesNotShipShipmentWhenCanceled() {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CANCELED,
            createdTimestamp = 456L,
            updateHistory = mutableListOf(),
            notes = mutableListOf(),
            expectedDeliveryDate = null,
            currentLocation = null
        )
        TrackingServer.addShipment(shipment)
        val updateData = UpdateData("SHIPPED", "123", 456L)

        // Act
        strategy.execute(updateData)

        // Assert
        val updatedShipment = TrackingServer.getShipment("123")
        assertNotNull(updatedShipment)
        assertEquals(ShipmentStatus.CANCELED, updatedShipment.status)
    }

    @Test
    fun executeHandlesMissingShipment() {
        // Arrange
        val updateData = UpdateData("SHIPPED", "123", 456L)

        // Act
        strategy.execute(updateData)

        // Assert
        val updatedShipment = TrackingServer.getShipment("123")
        // Should still be null, as ShippedStrategy does not create shipments
        assertEquals(null, updatedShipment)
    }
}