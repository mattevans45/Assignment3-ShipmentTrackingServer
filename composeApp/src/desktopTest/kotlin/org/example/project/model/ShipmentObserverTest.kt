package org.example.project.model

import kotlin.test.*

class DummyObserver : Observer {
    var notified = false
    override fun onShipmentUpdated(shipment: Shipment) {
        notified = true
    }

    override fun onShipmentCreated(shipment: Shipment) {

    }

    override fun onShipmentNotFound(shipmentId: String) {

    }
}

class ShipmentObserverTest {
    @Test
    fun addObserverAndNotify() {
        val shipment = StandardShipment("S1", ShipmentStatus.CREATED, 1000L)
        val observer = DummyObserver()
        shipment.addObserver(observer)
        shipment.addUpdate(ShippingUpdate("CREATED", "SHIPPED", 2000L))
        shipment.notifyObservers()
        assertTrue(observer.notified)
    }

    @Test
    fun removeObserver() {
        val shipment = StandardShipment("S1", ShipmentStatus.CREATED, 1000L)
        val observer = DummyObserver()
        shipment.addObserver(observer)
        shipment.removeObserver(observer)
        shipment.notifyObservers()
        assertFalse(observer.notified)
    }
}