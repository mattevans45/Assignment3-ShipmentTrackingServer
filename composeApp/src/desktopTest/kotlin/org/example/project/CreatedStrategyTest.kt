package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*
import kotlin.test.assertFailsWith

class CreatedStrategyTest {
    private lateinit var strategy: CreatedStrategy
    private lateinit var mockSimulator: TrackingSimulator

    @BeforeTest
    fun setUp() {
        mockSimulator = mock()
        TrackingSimulator.setTestInstance(mockSimulator)
        strategy = CreatedStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingSimulator.resetInstance()
    }

    @Test
    fun executeCreatesNewShipmentWhenNotExists() {
        // Arrange
        val updateData = UpdateData("CREATED", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(null)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).addShipment(any())
        verify(mockSimulator).notifyShipmentCreated(any())
        verify(mockSimulator).updateShipment(any())
        verify(mockSimulator).notifyObservers(any())
    }

    @Test
    fun executeUpdatesExistingShipmentWhenExists() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 456)
        val updateData = UpdateData("CREATED", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        verify(mockSimulator).notifyObservers(any())
    }
}