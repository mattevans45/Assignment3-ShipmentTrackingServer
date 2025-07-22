package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*

class CanceledStrategyTest {
    private lateinit var strategy: CanceledStrategy
    private lateinit var mockSimulator: TrackingSimulator

    @BeforeTest
    fun setUp() {
        mockSimulator = mock()
        TrackingSimulator.setTestInstance(mockSimulator)
        strategy = CanceledStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingSimulator.setTestInstance(null)
        TrackingSimulator.clearAllShipments()
    }

    @Test
    fun executeCancelsShipmentWhenNotDelivered() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 456)
        val updateData = UpdateData("CANCELED", "123", 456)

        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        assertEquals(ShipmentStatus.CANCELED, shipment.status)
    }

    @Test
    fun executeDoesNotCancelShipmentWhenDelivered() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.DELIVERED, 456)
        val updateData = UpdateData("CANCELED", "123", 456)

        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        assertEquals(ShipmentStatus.DELIVERED, shipment.status)
    }
}