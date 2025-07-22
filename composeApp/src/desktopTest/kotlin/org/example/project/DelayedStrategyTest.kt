package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*
import kotlin.test.assertFailsWith

class DelayedStrategyTest {
    private lateinit var strategy: DelayedStrategy
    private lateinit var mockSimulator: TrackingSimulator

    @BeforeTest
    fun setUp() {
        mockSimulator = mock()
        TrackingSimulator.setTestInstance(mockSimulator)
        strategy = DelayedStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingSimulator.setTestInstance(null)
        TrackingSimulator.clearAllShipments()
    }

    @Test
    fun executeDelaysShipmentWhenNotDelivered() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.SHIPPED, 456)
        val updateData = UpdateData("DELAYED", "123", 456, "1000")
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        assertEquals(ShipmentStatus.DELAYED, shipment.status)
        assertEquals(1000L, shipment.expectedDeliveryDateTimestamp)
    }

    @Test
    fun executeDoesNotDelayShipmentWhenDelivered() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.DELIVERED, 456)
        val updateData = UpdateData("DELAYED", "123", 456, "1000")
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        assertEquals(ShipmentStatus.DELIVERED, shipment.status)
    }

    @Test
    fun executeThrowsExceptionWhenShipmentNotFound() {
        // Arrange
        val updateData = UpdateData("DELAYED", "123", 456, "1000")
        whenever(mockSimulator.getShipment("123")).thenReturn(null)

        // Act & Assert
        assertFailsWith<IllegalStateException> {
            strategy.execute(updateData)
        }

        verify(mockSimulator).getShipment("123")

    }
}