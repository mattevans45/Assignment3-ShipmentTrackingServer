package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TrackingSimulatorTest {
    private lateinit var simulator: TrackingSimulator

    @BeforeTest
    fun setUp() {
        TrackingSimulator.setTestInstance(null)
        simulator = TrackingSimulator.getInstance()
    }

    @AfterTest
    fun tearDown() {
        TrackingSimulator.setTestInstance(null)
        simulator.clearAllShipments()
    }

    @Test
    fun getInstanceReturnsSingletonInstance() {
        // Act
        val instance1 = TrackingSimulator.getInstance()
        val instance2 = TrackingSimulator.getInstance()

        // Assert
        assertEquals(instance1, instance2)
    }

    @Test
    fun addShipmentAddsShipmentToCollection() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, System.currentTimeMillis())

        // Act
        simulator.addShipment(shipment)

        // Assert
        assertEquals(shipment, simulator.getShipment("123"))
    }

    @Test
    fun getShipmentReturnsNullWhenShipmentNotFound() {
        // Act
        val result = simulator.getShipment("nonexistent")

        // Assert
        assertNull(result)
    }

    @Test
    fun updateShipmentUpdatesExistingShipment() {
        // Arrange
        val shipment = Shipment("123", ShipmentStatus.CREATED, System.currentTimeMillis())
        simulator.addShipment(shipment)

        // Act
        shipment.updateStatus(ShipmentStatus.SHIPPED)
        simulator.updateShipment(shipment)

        // Assert
        val retrievedShipment = simulator.getShipment("123")
        assertNotNull(retrievedShipment)
        assertEquals(ShipmentStatus.SHIPPED, retrievedShipment.status)
    }

    @Test
    fun clearAllShipmentsRemovesAllShipments() {
        // Arrange
        simulator.addShipment(Shipment("123", ShipmentStatus.CREATED, System.currentTimeMillis()))
        simulator.addShipment(Shipment("456", ShipmentStatus.CREATED, System.currentTimeMillis()))

        // Act
        simulator.clearAllShipments()

        // Assert
        assertNull(simulator.getShipment("123"))
        assertNull(simulator.getShipment("456"))
    }
}