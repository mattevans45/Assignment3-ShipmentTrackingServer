package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*
import kotlin.test.assertFailsWith

class DeliveredStrategyTest {
    private lateinit var strategy: DeliveredStrategy
    private lateinit var mockSimulator: TrackingSimulator

    @BeforeTest
    fun setUp() {
        mockSimulator = mock()
        TrackingSimulator.setTestInstance(mockSimulator)
        strategy = DeliveredStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingSimulator.setTestInstance(null)
        TrackingSimulator.clearAllShipments()
    }

    @Test
    fun executeDeliversShipmentWhenNotCanceled() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.SHIPPED, 456)
        val updateData = UpdateData("DELIVERED", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        assertEquals(ShipmentStatus.DELIVERED, shipment.status)
    }

    @Test
    fun executeDoesNotDeliverShipmentWhenCanceled() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CANCELED, 456)
        val updateData = UpdateData("DELIVERED", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        assertEquals(ShipmentStatus.CANCELED, shipment.status)
    }

    @Test
    fun executeThrowsExceptionWhenShipmentNotFound() {
        // Arrange
        val updateData = UpdateData("DELIVERED", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(null)

        // Act & Assert
        assertFailsWith<IllegalStateException> {
            strategy.execute(updateData)
        }

        verify(mockSimulator).getShipment("123")

    }
}