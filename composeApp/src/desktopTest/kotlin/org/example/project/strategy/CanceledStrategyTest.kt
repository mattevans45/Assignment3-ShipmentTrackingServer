package org.example.project.strategy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.example.project.model.*
import org.example.project.server.TrackingServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CanceledStrategyTest {
    private lateinit var strategy: CanceledStrategy

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        TrackingServer.initialize(CoroutineScope(SupervisorJob()))
        strategy = CanceledStrategy()
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun executeCancelsShipmentWhenNotDelivered() {
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L,
            expectedDeliveryDate = 456L + 86400000,
            currentLocation = "Origin",
            updateHistory = mutableListOf(),
            notes = mutableListOf()
        )
        TrackingServer.addShipment(shipment)
        val updateData = UpdateData("CANCELED", "123", 456L)

        strategy.execute(updateData)

        val updatedShipment = TrackingServer.getShipment("123")
        assertEquals(ShipmentStatus.CANCELED, updatedShipment?.status)
    }

}