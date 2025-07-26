package org.example.project.model

import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ShipmentSubclassesTest {

    @Test
    fun standardShipment_validateExpectedDelivery_alwaysNull() {
        val shipment = StandardShipment(
            id = "S1",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 1000L
        )
        assertNull(shipment.validateExpectedDelivery())
    }

    @Test
    fun standardShipment_isViolationRelevant_alwaysFalse() {
        val shipment = StandardShipment(
            id = "S1",
            status = ShipmentStatus.DELAYED,
            createdTimestamp = 1000L
        )
        assertFalse(shipment.isViolationRelevant())
    }

    @Test
    fun bulkShipment_validateExpectedDelivery_tooSoon() {
        val created = 1000L
        val expected = created + TimeUnit.DAYS.toMillis(2) // less than 3 days
        val shipment = BulkShipment(
            id = "B1",
            status = ShipmentStatus.CREATED,
            createdTimestamp = created,
            expectedDeliveryDate = expected
        )
        val msg = shipment.validateExpectedDelivery()
        assertTrue(msg?.contains("bulk shipment") == true)
    }

    @Test
    fun bulkShipment_validateExpectedDelivery_ok() {
        val created = 1000L
        val expected = created + TimeUnit.DAYS.toMillis(3)
        val shipment = BulkShipment(
            id = "B2",
            status = ShipmentStatus.CREATED,
            createdTimestamp = created,
            expectedDeliveryDate = expected
        )
        assertNull(shipment.validateExpectedDelivery())
    }

    @Test
    fun bulkShipment_isViolationRelevant_notDelayed() {
        val shipment = BulkShipment(
            id = "B3",
            status = ShipmentStatus.SHIPPED,
            createdTimestamp = 1000L
        )
        assertTrue(shipment.isViolationRelevant())
    }

    @Test
    fun bulkShipment_isViolationRelevant_delayed() {
        val shipment = BulkShipment(
            id = "B4",
            status = ShipmentStatus.DELAYED,
            createdTimestamp = 1000L
        )
        assertFalse(shipment.isViolationRelevant())
    }

    @Test
    fun expressShipment_validateExpectedDelivery_tooLate() {
        val created = 1000L
        val expected = created + TimeUnit.DAYS.toMillis(4) // more than 3 days
        val shipment = ExpressShipment(
            id = "E1",
            status = ShipmentStatus.CREATED,
            createdTimestamp = created,
            expectedDeliveryDate = expected
        )
        val msg = shipment.validateExpectedDelivery()
        assertTrue(msg?.contains("express shipment") == true)
    }

    @Test
    fun expressShipment_validateExpectedDelivery_ok() {
        val created = 1000L
        val expected = created + TimeUnit.DAYS.toMillis(3)
        val shipment = ExpressShipment(
            id = "E2",
            status = ShipmentStatus.CREATED,
            createdTimestamp = created,
            expectedDeliveryDate = expected
        )
        assertNull(shipment.validateExpectedDelivery())
    }

    @Test
    fun expressShipment_isViolationRelevant_notDelayed() {
        val shipment = ExpressShipment(
            id = "E3",
            status = ShipmentStatus.SHIPPED,
            createdTimestamp = 1000L
        )
        assertTrue(shipment.isViolationRelevant())
    }

    @Test
    fun expressShipment_isViolationRelevant_delayed() {
        val shipment = ExpressShipment(
            id = "E4",
            status = ShipmentStatus.DELAYED,
            createdTimestamp = 1000L
        )
        assertFalse(shipment.isViolationRelevant())
    }

    @Test
    fun overnightShipment_validateExpectedDelivery_tooLate() {
        val created = 1000L
        val expected = created + TimeUnit.DAYS.toMillis(2) // more than 1 day
        val shipment = OvernightShipment(
            id = "O1",
            status = ShipmentStatus.CREATED,
            createdTimestamp = created,
            expectedDeliveryDate = expected
        )
        val msg = shipment.validateExpectedDelivery()
        assertTrue(msg?.contains("overnight shipment") == true)
    }

    @Test
    fun overnightShipment_validateExpectedDelivery_ok() {
        val created = 1000L
        val expected = created + TimeUnit.DAYS.toMillis(1)
        val shipment = OvernightShipment(
            id = "O2",
            status = ShipmentStatus.CREATED,
            createdTimestamp = created,
            expectedDeliveryDate = expected
        )
        assertNull(shipment.validateExpectedDelivery())
    }

    @Test
    fun overnightShipment_isViolationRelevant_notDelayed() {
        val shipment = OvernightShipment(
            id = "O3",
            status = ShipmentStatus.SHIPPED,
            createdTimestamp = 1000L
        )
        assertTrue(shipment.isViolationRelevant())
    }

    @Test
    fun overnightShipment_isViolationRelevant_delayed() {
        val shipment = OvernightShipment(
            id = "O4",
            status = ShipmentStatus.DELAYED,
            createdTimestamp = 1000L
        )
        assertFalse(shipment.isViolationRelevant())
    }
}