package org.example.project

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrackerViewHelperTest {
    private lateinit var viewHelper: TrackerViewHelper

    @BeforeEach
    fun setUp() {
        viewHelper = TrackerViewHelper()
    }

    @AfterEach
    fun tearDown() {
        TrackingSimulator.resetInstance()
    }

    @Test
    fun `trackShipment adds shipment to tracked list`() {
        // Arrange
        val shipmentId = "123"
        val shipment = Shipment(shipmentId, ShipmentStatus.CREATED, 456)
        val mockSimulator = mock<TrackingSimulator>()
        whenever(mockSimulator.getShipment(shipmentId)).thenReturn(shipment)

        // Set the mock instance
        TrackingSimulator.setTestInstance(mockSimulator)

        // Act
        viewHelper.trackShipment(shipmentId)

        // Assert
        val trackedShipments = viewHelper.trackedShipmentData.value
        assertEquals(1, trackedShipments.size)
        assertEquals(shipment, trackedShipments[shipmentId])

        // Clean up
        TrackingSimulator.resetInstance()
    }
    
    @Test
    fun `stopTracking removes shipment from tracked list`() {
        // Arrange
        val shipmentId = "123"
        val shipment = Shipment(shipmentId, ShipmentStatus.CREATED, 456)
        val mockSimulator = mock<TrackingSimulator>()
        whenever(mockSimulator.getShipment(shipmentId)).thenReturn(shipment)
        

        viewHelper.trackShipment(shipmentId)
        
        // Act
        viewHelper.stopTracking(shipmentId)
        
        // Assert
        val trackedShipments = viewHelper.trackedShipmentData.value
        assertEquals(0, trackedShipments.size)
        assertTrue(shipmentId !in trackedShipments)
    }
    
    @Test
    fun `onShipmentUpdated updates tracked shipment in state`() {
        // Arrange
        val shipmentId = "123"
        val originalShipment = Shipment(shipmentId, ShipmentStatus.CREATED, 456)
        val updatedShipment = Shipment(shipmentId, ShipmentStatus.SHIPPED, 456)
        
        // Add to tracked shipments using reflection
        val trackedShipmentsField = TrackerViewHelper::class.java.getDeclaredField("trackedShipments")
        trackedShipmentsField.isAccessible = true
        (trackedShipmentsField.get(viewHelper) as MutableSet<String>).add(shipmentId)
        
        val trackedShipmentDataField = TrackerViewHelper::class.java.getDeclaredField("_trackedShipmentData")
        trackedShipmentDataField.isAccessible = true
        (trackedShipmentDataField.get(viewHelper) as androidx.compose.runtime.MutableState<Map<String, Shipment>>)
            .value = mapOf(shipmentId to originalShipment)
        
        // Act
        viewHelper.onShipmentUpdated(updatedShipment)
        
        // Assert
        val trackedShipments = viewHelper.trackedShipmentData.value
        assertEquals(updatedShipment, trackedShipments[shipmentId])
    }
    
    @Test
    fun `onShipmentCreated does not modify state`() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 456)
        val initialState = viewHelper.trackedShipmentData.value
        
        // Act
        viewHelper.onShipmentCreated(shipment)
        
        // Assert
        assertEquals(initialState, viewHelper.trackedShipmentData.value)
    }
    
    @Test
    fun `resetSimulation clears all tracked shipments`() {
        // Arrange
        val shipmentId = "123"
        val shipment = Shipment(shipmentId, ShipmentStatus.CREATED, 456)
        val mockSimulator = mock<TrackingSimulator>()
        whenever(mockSimulator.getShipment(shipmentId)).thenReturn(shipment)
        

        viewHelper.trackShipment(shipmentId)
        
        // Act
        viewHelper.resetSimulation()
        
        // Assert
        val trackedShipments = viewHelper.trackedShipmentData.value
        assertEquals(0, trackedShipments.size)
    }
}