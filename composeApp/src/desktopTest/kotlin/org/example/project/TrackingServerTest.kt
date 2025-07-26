package org.example.project

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.example.project.model.*
import org.example.project.server.TrackingServer
import kotlin.test.*

class TrackingServerTest {

    @BeforeTest
    fun setUp() {
        // Ensure a clean state before each test
        TrackingServer.clearAllShipments()
        val scope = CoroutineScope(SupervisorJob())
        TrackingServer.initialize(scope)
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun addShipmentShouldAddNewShipmentToServer() {
        // Arrange
        val shipment = StandardShipment("s1", ShipmentStatus.CREATED, 1L)

        // Act
        TrackingServer.addShipment(shipment)

        // Assert
        assertEquals(shipment, TrackingServer.getShipment("s1"))
    }

    @Test
    fun updateShipmentShouldModifyExistingShipment() {
        // Arrange
        val shipment = StandardShipment("s1", ShipmentStatus.CREATED, 1L)
        TrackingServer.addShipment(shipment)

        // Act
        shipment.status = ShipmentStatus.SHIPPED
        TrackingServer.updateShipment(shipment)

        // Assert
        val updatedShipment = TrackingServer.getShipment("s1")
        assertEquals(ShipmentStatus.SHIPPED, updatedShipment?.status)
    }

    @Test
    fun getAllShipmentsShouldReturnAllShipments() {
        // Arrange
        TrackingServer.addShipment(StandardShipment("s1", ShipmentStatus.CREATED, 1L))
        TrackingServer.addShipment(ExpressShipment("s2", ShipmentStatus.CREATED, 2L))

        // Act
        val allShipments = TrackingServer.getAllShipments()

        // Assert
        assertEquals(2, allShipments.size)
    }

    @Test
    fun removeShouldDeleteShipment() {
        // Arrange
        val shipment = StandardShipment("s1", ShipmentStatus.CREATED, 1L)
        TrackingServer.addShipment(shipment)

        // Act
        val wasRemoved = TrackingServer.remove("s1")

        // Assert
        assertTrue(wasRemoved)
        assertNull(TrackingServer.getShipment("s1"))
    }

    @Test
    fun getShipmentShouldReturnNullForNonExistentShipment() {
        // Act
        val shipment = TrackingServer.getShipment("nonexistent")

        // Assert
        assertNull(shipment)
    }

    @Test
    fun removeShouldReturnFalseForNonExistentShipment() {
        // Act
        val wasRemoved = TrackingServer.remove("nonexistent")

        // Assert
        assertFalse(wasRemoved)
    }

    @Test
    fun clearAllShipmentsShouldRemoveAllShipments() {
        // Arrange
        TrackingServer.addShipment(StandardShipment("s1", ShipmentStatus.CREATED, 1L))
        TrackingServer.addShipment(StandardShipment("s2", ShipmentStatus.CREATED, 2L))

        // Act
        TrackingServer.clearAllShipments()

        // Assert
        assertEquals(0, TrackingServer.getAllShipments().size)
    }
}