package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import org.mockito.kotlin.*

class AbstractUpdateStrategyTest {
    private lateinit var strategy: TestUpdateStrategy
    private lateinit var mockSimulator: TrackingServer

    @BeforeTest
    fun setUp() {
        mockSimulator = mock()
        strategy = TestUpdateStrategy()
        TrackingSimulator.setTestInstance(mockSimulator)
    }
    @AfterTest
    fun tearDown() {
        TrackingSimulator.setTestInstance(null)
        TrackingSimulator.clearAllShipments()
    }
    @Test
    fun executeProcessesUpdateAndCreatesShippingUpdate() {
        // Arrange
        val updateData = UpdateData("TEST", "123", 456)
        val shipment = Shipment("123", ShipmentStatus.CREATED, 123)

        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)

        // Act
        strategy.execute(updateData)

        // Assert
        verify(mockSimulator).getShipment("123")
        verify(mockSimulator).updateShipment(any())

        // Verify the strategy's processUpdate was called
        assertEquals(1, strategy.processUpdateCallCount)
        assertEquals(ShipmentStatus.CREATED, strategy.lastProcessedStatus)
    }

    @Test
    fun executeHandlesCaseWhenShipmentNotFound() {
        // Arrange
        val updateData = UpdateData("CREATED", "123", 456)
        whenever(mockSimulator.getShipment("123")).thenReturn(null)

        // Act & Assert
        try {
            strategy.execute(updateData)
            fail("Expected IllegalStateException to be thrown")
        } catch (e: IllegalStateException) {
            assertEquals("Shipment not found: 123", e.message)
        }

        // Assert
        verify(mockSimulator).getShipment("123")
        verify(mockSimulator, never()).updateShipment(any())
        // Removed: verify(mockSimulator, never()).notifyObservers(any())
    }

    // Test implementation of AbstractUpdateStrategy
    private class TestUpdateStrategy : AbstractUpdateStrategy("TEST") {
        var processUpdateCallCount = 0
        var lastProcessedStatus: ShipmentStatus? = null

        override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
            processUpdateCallCount++
            lastProcessedStatus = shipment.status // Use property, not getStatus()
        }

        override fun validateUpdate(updateData: UpdateData): Boolean {
            // No-op for test
            return true
        }
    }
}
