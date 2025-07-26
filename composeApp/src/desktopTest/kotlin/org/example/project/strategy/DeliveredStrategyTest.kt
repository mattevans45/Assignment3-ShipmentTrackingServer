package org.example.project.strategy

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.example.project.model.*
import org.example.project.server.TrackingServer

class DeliveredStrategyTest {
    private lateinit var strategy: DeliveredStrategy

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        strategy = DeliveredStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun executeDeliversShipmentWhenNotCanceled() {

        val shipment =
                StandardShipment(
                        id = "123",
                        status = ShipmentStatus.SHIPPED,
                        createdTimestamp = 456L,
                        expectedDeliveryDate = 456L + 86400000,
                        currentLocation = "In Transit",
                        updateHistory = mutableListOf(),
                        notes = mutableListOf()
                )
        TrackingServer.addShipment(shipment)
        val updateData = UpdateData("DELIVERED", "123", 456L)

        strategy.execute(updateData)

        val updatedShipment = TrackingServer.getShipment("123")
        assertNotNull(updatedShipment)
        assertEquals(ShipmentStatus.DELIVERED, updatedShipment.status)
    }

    @Test
    fun executeHandlesMissingShipment() {

        val updateData = UpdateData("DELIVERED", "123", 456L)

        assertEquals(null, TrackingServer.getShipment("123"))

        strategy.execute(updateData)

        assertEquals(null, TrackingServer.getShipment("123"))
    }

    @Test
    fun executeWithNonDeliveredTypeStillDelivers() {
        val shipment =
                StandardShipment(
                        id = "123",
                        status = ShipmentStatus.SHIPPED,
                        createdTimestamp = 456L,
                        expectedDeliveryDate = 456L + 86400000,
                        currentLocation = "In Transit",
                        updateHistory = mutableListOf(),
                        notes = mutableListOf()
                )
        TrackingServer.addShipment(shipment)
        val updateData = UpdateData("SOMETHINGELSE", "123", 456L)

        strategy.execute(updateData)

        val updatedShipment = TrackingServer.getShipment("123")
        assertNotNull(updatedShipment)
        assertEquals(ShipmentStatus.DELIVERED, updatedShipment.status)
    }
}
