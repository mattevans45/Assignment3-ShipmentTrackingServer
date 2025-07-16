package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*

class AbstractUpdateStrategyTest {
    private lateinit var strategy: TestUpdateStrategy
    private lateinit var mockSimulator: TrackingSimulator
    
    @BeforeTest
    fun setUp() {
        mockSimulator = mock()
        TrackingSimulator.setTestInstance(mockSimulator)
        strategy = TestUpdateStrategy()
    }
    
    @AfterTest
    fun tearDown() {
        TrackingSimulator.resetInstance()
    }
    
    @Test
    fun executeProcessesUpdateAndCreatesShippingUpdate() {
        // Arrange
        val updateData = UpdateData("TEST", "123", 456)
        val shipment = Shipment("123", ShipmentStatus.CREATED, 123)
        val shipmentCopy = shipment.copy()
        
        whenever(mockSimulator.getShipment("123")).thenReturn(shipment)
        
        // Act
        strategy.execute(updateData)
        
        // Assert
        verify(mockSimulator).getShipment("123")
        verify(mockSimulator).updateShipment(any())
        verify(mockSimulator).notifyObservers(any())
        
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
            // If we reach here, the test should fail
            kotlin.test.fail("Expected IllegalStateException to be thrown")
        } catch (e: IllegalStateException) {
            // Expected behavior
            assertEquals("Shipment not found: 123", e.message)
        }

        // Assert
        verify(mockSimulator).getShipment("123")
        verify(mockSimulator, never()).updateShipment(any())
        verify(mockSimulator, never()).notifyObservers(any())
    }
    
    // Test implementation of AbstractUpdateStrategy
    private class TestUpdateStrategy : AbstractUpdateStrategy("TEST") {
        var processUpdateCallCount = 0
        var lastProcessedStatus: ShipmentStatus? = null
        
        override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
            processUpdateCallCount++
            lastProcessedStatus = shipment.getStatus()
        }
        
        override fun validateUpdate(updateData: UpdateData) {
            // No-op for test
        }
    }
}