package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*
import kotlin.test.assertFailsWith

class LostStrategyTest {
    private lateinit var strategy: LostStrategy
    private lateinit var mockSimulator: TrackingSimulator

    @BeforeTest
    fun setUp() {
        mockSimulator = mock()
        TrackingSimulator.setTestInstance(mockSimulator)
        strategy = LostStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingSimulator.resetInstance()
    }

    @Test
    fun executeMarksShipmentAsLostWhenNotDelivered() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.SHIPPED, 456)
        val updateData = UpdateData("LOST", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        verify(mockSimulator).notifyObservers(any())
    }

    @Test
    fun executeDoesNotMarkShipmentAsLostWhenDelivered() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.DELIVERED, 456)
        val updateData = UpdateData("LOST", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        verify(mockSimulator).notifyObservers(any())
    }

    @Test
    fun executeThrowsExceptionWhenShipmentNotFound() {
        // Arrange
        val updateData = UpdateData("LOST", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(null)

        // Act & Assert
        assertFailsWith<IllegalStateException> {
            strategy.execute(updateData)
        }

        verify(mockSimulator).getShipment("123")
        verify(mockSimulator).notifyShipmentNotFound("123")
    }
}