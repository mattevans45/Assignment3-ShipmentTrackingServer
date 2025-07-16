package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.mockito.kotlin.*

class TrackingSimulatorTest {
    private lateinit var simulator: TrackingSimulator
    private lateinit var mockObserver: Observer
    
    @BeforeTest
    fun setUp() {
        TrackingSimulator.resetInstance()
        simulator = TrackingSimulator.getInstance()
        mockObserver = mock()
    }

    @AfterTest
    fun tearDown() {
        TrackingSimulator.resetInstance()
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
        val originalShipment = Shipment("123", ShipmentStatus.CREATED, System.currentTimeMillis())
        simulator.addShipment(originalShipment)
        
        val updatedShipment = originalShipment.copy()
        updatedShipment.updateStatus(ShipmentStatus.SHIPPED)
        
        // Act
        simulator.updateShipment(updatedShipment)
        
        // Assert
        val retrievedShipment = simulator.getShipment("123")
        assertNotNull(retrievedShipment)
        assertEquals(ShipmentStatus.SHIPPED, retrievedShipment.getStatus())
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
    
    @Test
    fun addObserverAddsObserverToCollection() {
        // Act
        simulator.addObserver(mockObserver)
        
        // Assert
        // We'll verify this indirectly by triggering a notification
        val shipment = Shipment("123", ShipmentStatus.CREATED, System.currentTimeMillis())
        simulator.notifyObservers(shipment)
        verify(mockObserver).onShipmentUpdated(shipment)
    }
    
    @Test
    fun removeObserverRemovesObserverFromCollection() {
        // Arrange
        simulator.addObserver(mockObserver)
        
        // Act
        simulator.removeObserver(mockObserver)
        
        // Assert
        val shipment = Shipment("123", ShipmentStatus.CREATED, System.currentTimeMillis())
        simulator.notifyObservers(shipment)
        verifyNoInteractions(mockObserver)
    }
    
    @Test
    fun notifyObserversCallsOnShipmentUpdatedOnAllObservers() {
        // Arrange
        val mockObserver1 = mock<Observer>()
        val mockObserver2 = mock<Observer>()
        simulator.addObserver(mockObserver1)
        simulator.addObserver(mockObserver2)
        
        // Act
        val shipment = Shipment("123", ShipmentStatus.CREATED, System.currentTimeMillis())
        simulator.notifyObservers(shipment)
        
        // Assert
        verify(mockObserver1).onShipmentUpdated(shipment)
        verify(mockObserver2).onShipmentUpdated(shipment)
    }
}