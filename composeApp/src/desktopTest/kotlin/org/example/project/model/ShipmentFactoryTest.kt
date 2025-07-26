package org.example.project.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShipmentFactoryTest {

    @Test
    fun createStandardShipment() {
        val shipment = ShipmentFactory.create(
            ShipmentType.STANDARD, "S1", ShipmentStatus.CREATED, 1000L, 2000L
        )
        assertTrue(shipment is StandardShipment)
        assertEquals("S1", shipment.id)
        assertEquals(ShipmentStatus.CREATED, shipment.status)
        assertEquals(1000L, shipment.createdTimestamp)
        assertEquals(2000L, shipment.expectedDeliveryDate)
    }

    @Test
    fun createExpressShipment() {
        val shipment = ShipmentFactory.create(
            ShipmentType.EXPRESS, "E1", ShipmentStatus.SHIPPED, 2000L, 3000L
        )
        assertTrue(shipment is ExpressShipment)
        assertEquals("E1", shipment.id)
        assertEquals(ShipmentStatus.SHIPPED, shipment.status)
        assertEquals(2000L, shipment.createdTimestamp)
        assertEquals(3000L, shipment.expectedDeliveryDate)
    }

    @Test
    fun createOvernightShipment() {
        val shipment = ShipmentFactory.create(
            ShipmentType.OVERNIGHT, "O1", ShipmentStatus.DELIVERED, 3000L, 4000L
        )
        assertTrue(shipment is OvernightShipment)
        assertEquals("O1", shipment.id)
        assertEquals(ShipmentStatus.DELIVERED, shipment.status)
        assertEquals(3000L, shipment.createdTimestamp)
        assertEquals(4000L, shipment.expectedDeliveryDate)
    }

    @Test
    fun createBulkShipment() {
        val shipment = ShipmentFactory.create(
            ShipmentType.BULK, "B1", ShipmentStatus.LOST, 4000L, 5000L
        )
        assertTrue(shipment is BulkShipment)
        assertEquals("B1", shipment.id)
        assertEquals(ShipmentStatus.LOST, shipment.status)
        assertEquals(4000L, shipment.createdTimestamp)
        assertEquals(5000L, shipment.expectedDeliveryDate)
    }
}