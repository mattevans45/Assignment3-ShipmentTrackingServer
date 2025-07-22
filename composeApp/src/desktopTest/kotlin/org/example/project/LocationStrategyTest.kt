package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*
import kotlin.test.assertFailsWith

class LocationStrategyTest {
    private lateinit var strategy: LocationStrategy
    private lateinit var mockSimulator: TrackingSimulator

    @BeforeTest
    fun setUp() {
        mockSimulator = mock()
        TrackingSimulator.setTestInstance(mockSimulator)
        strategy = LocationStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingSimulator.setTestInstance(null)
        TrackingSimulator.clearAllShipments()
    }

    @Test
    fun executeUpdatesLocationWhenShipmentExists() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.SHIPPED, 456)
        val updateData = UpdateData("LOCATION", "123", 456, "New York")
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).updateShipment(any())
        assertEquals("New York", shipment.currentLocation)
    }

    @Test
    fun executeThrowsExceptionWhenShipmentNotFound() {
        // Arrange
        val updateData = UpdateData("LOCATION", "123", 456, "New York")
        whenever(mockSimulator.getShipment("123")).thenReturn(null)

        // Act & Assert
        assertFailsWith<IllegalStateException> {
            strategy.execute(updateData)
        }

        verify(mockSimulator).getShipment("123")

    }
}