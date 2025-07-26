package org.example.project.strategy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.example.project.model.*
import org.example.project.server.TrackingServer
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import kotlin.test.*

class AbstractUpdateStrategyTest {

    private lateinit var testStrategy: TestUpdateStrategy
    private lateinit var createdStrategy: CreatedStrategy
    private lateinit var nonCreatedStrategy: TestNonCreatedStrategy

    @BeforeTest
    fun setUp() {
        val scope = CoroutineScope(SupervisorJob())
        TrackingServer.initialize(scope)
        TrackingServer.clearAllShipments()
        testStrategy = TestUpdateStrategy()
        createdStrategy = CreatedStrategy()
        nonCreatedStrategy = TestNonCreatedStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    // Test execute method with valid data and existing shipment
    @Test
    fun executeWithValidDataAndExistingShipment() {
        // Arrange
        val shipment = StandardShipment("SHIP123", ShipmentStatus.CREATED, 1234567890L)
        TrackingServer.addShipment(shipment)

        val updateData = UpdateData("SHIPPED", "SHIP123", 1234567890L)

        // Act
        testStrategy.execute(updateData)

        // Assert
        assertTrue(testStrategy.processUpdateCalled)
        assertEquals(shipment, testStrategy.lastShipment)
        assertEquals(updateData, testStrategy.lastUpdateData)
        assertEquals(1, shipment.updateHistory.size)
    }

    // Test execute method with null shipment and CreatedStrategy
    @Test
    fun executeWithNullShipmentAndCreatedStrategy() {
        // Arrange
        val updateData = UpdateData("CREATED", "SHIP456", 1234567890L, "express")

        // Act
        createdStrategy.execute(updateData)

        // Assert
        val createdShipment = TrackingServer.getShipment("SHIP456")
        assertNotNull(createdShipment)
        assertEquals(ShipmentStatus.CREATED, createdShipment.status)
    }


    // Test validateUpdate with valid data
    @Test
    fun validateUpdateWithValidData() {
        // Arrange
        val validUpdateData = UpdateData("SHIPPED", "SHIP123", 1234567890L)

        // Act & Assert - should not throw exception
        assertDoesNotThrow { testStrategy.validateUpdate(validUpdateData) }
    }

    // Test validateUpdate with invalid data - empty update type
    @Test
    fun validateUpdateWithEmptyUpdateType() {
        // Arrange
        val invalidUpdateData = UpdateData("", "SHIP123", 1234567890L)

        // Act & Assert
        assertFailsWith<IllegalArgumentException> {
            testStrategy.validateUpdate(invalidUpdateData)
        }
    }

    // Test validateUpdate with invalid data - blank shipment ID
    @Test
    fun validateUpdateWithBlankShipmentId() {
        // Arrange
        val invalidUpdateData = UpdateData("SHIPPED", "   ", 1234567890L)

        // Act & Assert
        assertFailsWith<IllegalArgumentException> {
            testStrategy.validateUpdate(invalidUpdateData)
        }
    }

    // Test validateUpdate with invalid data - zero timestamp
    @Test
    fun validateUpdateWithZeroTimestamp() {
        // Arrange
        val invalidUpdateData = UpdateData("SHIPPED", "SHIP123", 0L)

        // Act & Assert
        assertFailsWith<IllegalArgumentException> {
            testStrategy.validateUpdate(invalidUpdateData)
        }
    }

    // Test createShippingUpdate method
    @Test
    fun createShippingUpdateWithNormalUpdate() {
        // Arrange
        val shipment = StandardShipment("SHIP123", ShipmentStatus.CREATED, 1234567890L)
        shipment.currentLocation = "Chicago"
        TrackingServer.addShipment(shipment)

        val updateData = UpdateData("SHIPPED", "SHIP123", 1234567890L)

        // Act
        testStrategy.execute(updateData)

        // Assert
        val shippingUpdate = shipment.updateHistory.last()
        assertEquals(ShipmentStatus.CREATED.toString(), shippingUpdate.previousStatus)
        assertEquals(shipment.status.toString(), shippingUpdate.newStatus)
        assertEquals(updateData.getTimestamp(), shippingUpdate.timestamp)
        assertEquals(shipment.currentLocation, shippingUpdate.location)
        assertNull(shippingUpdate.notes)
    }

    // Test createShippingUpdate with NOTEADDED update type
    @Test
    fun createShippingUpdateWithNoteAddedUpdate() {
        // Arrange
        val shipment = StandardShipment("SHIP123", ShipmentStatus.CREATED, 1234567890L)
        TrackingServer.addShipment(shipment)

        val updateData = UpdateData("NOTEADDED", "SHIP123", 1234567890L, "Package delayed due to weather")

        // Act
        testStrategy.execute(updateData)

        // Assert
        val shippingUpdate = shipment.updateHistory.last()
        assertEquals("Package delayed due to weather", shippingUpdate.notes)
    }

    // Test that shipment is updated in TrackingServer
    @Test
    fun executeUpdatesShipmentInTrackingServer() {
        // Arrange
        val shipment = StandardShipment("SHIP123", ShipmentStatus.CREATED, 1234567890L)
        TrackingServer.addShipment(shipment)

        val updateData = UpdateData("SHIPPED", "SHIP123", 1234567890L)

        // Act
        testStrategy.execute(updateData)

        // Assert
        val updatedShipment = TrackingServer.getShipment("SHIP123")
        assertNotNull(updatedShipment)
        assertEquals(1, updatedShipment.updateHistory.size)
    }

    // Test execute with multiple updates
    @Test
    fun executeWithMultipleUpdates() {
        // Arrange
        val shipment = StandardShipment("SHIP123", ShipmentStatus.CREATED, 1234567890L)
        TrackingServer.addShipment(shipment)

        val updateData1 = UpdateData("SHIPPED", "SHIP123", 1234567890L)
        val updateData2 = UpdateData("LOCATION", "SHIP123", 1234567891L, "Chicago")

        // Act
        testStrategy.execute(updateData1)
        testStrategy.execute(updateData2)

        // Assert
        val updatedShipment = TrackingServer.getShipment("SHIP123")
        assertNotNull(updatedShipment)
        assertEquals(2, updatedShipment.updateHistory.size)
    }


    // Test that execute calls validateUpdate first
    @Test
    fun executeCallsValidateUpdateFirst() {
        // Arrange
        val invalidUpdateData = UpdateData("", "SHIP123", 1234567890L)

        // Act & Assert
        assertFailsWith<IllegalArgumentException> {
            testStrategy.execute(invalidUpdateData)
        }
        // processUpdate should not be called if validation fails
        assertFalse(testStrategy.processUpdateCalled)
    }

    // Test that execute handles CreatedStrategy correctly
    @Test
    fun executeWithCreatedStrategyAndNullShipment() {
        // Arrange
        val updateData = UpdateData("CREATED", "NEW_SHIP", 1234567890L, "standard")

        // Act
        createdStrategy.execute(updateData)

        // Assert
        val newShipment = TrackingServer.getShipment("NEW_SHIP")
        assertNotNull(newShipment)
        assertEquals("NEW_SHIP", newShipment.id)
        assertEquals(ShipmentStatus.CREATED, newShipment.status)
    }

    // Helper test classes
    private class TestUpdateStrategy : AbstractUpdateStrategy() {
        var processUpdateCalled = false
        var lastShipment: Shipment? = null
        var lastUpdateData: UpdateData? = null

        override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
            processUpdateCalled = true
            lastShipment = shipment
            lastUpdateData = updateData
            shipment?.status = ShipmentStatus.SHIPPED
        }

        // Expose protected method for testing
        public override fun validateUpdate(updateData: UpdateData) {
            super.validateUpdate(updateData)
        }
    }

    private class TestNonCreatedStrategy : AbstractUpdateStrategy() {
        var processUpdateCalled = false
        var lastShipment: Shipment? = null

        override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
            processUpdateCalled = true
            lastShipment = shipment
        }
    }
}