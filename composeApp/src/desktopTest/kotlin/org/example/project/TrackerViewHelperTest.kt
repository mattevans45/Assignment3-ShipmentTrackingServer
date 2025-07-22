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
        TrackingSimulator.setTestInstance(null)
        TrackingSimulator.clearAllShipments()
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
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(1, trackedShipments.size)
        assertEquals(shipment, trackedShipments[shipmentId])
    }

    @Test
    fun `stopTracking removes shipment from tracked list`() {
        // Arrange
        val shipmentId = "123"
        val shipment = Shipment(shipmentId, ShipmentStatus.CREATED, 456)
        val mockSimulator = mock<TrackingSimulator>()
        whenever(mockSimulator.getShipment(shipmentId)).thenReturn(shipment)

        TrackingSimulator.setTestInstance(mockSimulator)
        viewHelper.trackShipment(shipmentId)

        // Act
        viewHelper.stopTracking(shipmentId)

        // Assert
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(0, trackedShipments.size)
        assertTrue(shipmentId !in trackedShipments)
    }

    @Test
    fun `onShipmentUpdated updates tracked shipment in state`() {
        // Arrange
        val shipmentId = "123"
        val shipment = Shipment(shipmentId, ShipmentStatus.CREATED, 456)
        viewHelper.trackShipment(shipmentId)

        // Act
        shipment.updateStatus(ShipmentStatus.SHIPPED)
        viewHelper.onShipmentUpdated(shipment)

        // Assert
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(ShipmentStatus.SHIPPED, trackedShipments[shipmentId]?.status)
    }

    @Test
    fun `onShipmentCreated does not modify state if not tracked`() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, 456)
        val initialState = viewHelper.trackedShipmentData.toMap()

        // Act
        viewHelper.onShipmentCreated(shipment)

        // Assert
        assertEquals(initialState, viewHelper.trackedShipmentData.toMap())
    }

    @Test
    fun `resetSimulation clears all tracked shipments`() {
        // Arrange
        val shipmentId = "123"
        val shipment = Shipment(shipmentId, ShipmentStatus.CREATED, 456)
        val mockSimulator = mock<TrackingSimulator>()
        whenever(mockSimulator.getShipment(shipmentId)).thenReturn(shipment)

        TrackingSimulator.setTestInstance(mockSimulator)
        viewHelper.trackShipment(shipmentId)

        // Act
        viewHelper.resetSimulation()

        // Assert
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(0, trackedShipments.size)
    }
}