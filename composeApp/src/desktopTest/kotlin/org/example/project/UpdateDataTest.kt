package org.example.project

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UpdateDataTest {

    @Test
    fun constructorWithAllParametersSetsAllProperties() {
        // Arrange
        val updateType = "LOCATION"
        val shipmentId = "123"
        val timestamp = 1699123456789L
        val otherInfo = "New York"

        // Act
        val updateData = UpdateData(updateType, shipmentId, timestamp, otherInfo)

        // Assert
        assertEquals(updateType, updateData.getUpdateType())
        assertEquals(shipmentId, updateData.getShipmentId())
        assertEquals(timestamp, updateData.getTimestamp())
        assertEquals(otherInfo, updateData.getOtherInfo())
    }

    @Test
    fun constructorWithoutOtherInfoSetsNullForOtherInfo() {
        // Arrange
        val updateType = "SHIPPED"
        val shipmentId = "456"
        val timestamp = 1699123456789L

        // Act
        val updateData = UpdateData(updateType, shipmentId, timestamp)

        // Assert
        assertEquals(updateType, updateData.getUpdateType())
        assertEquals(shipmentId, updateData.getShipmentId())
        assertEquals(timestamp, updateData.getTimestamp())
        assertNull(updateData.getOtherInfo())
    }

    @Test
    fun constructorHandlesEmptyStrings() {
        // Arrange
        val updateType = ""
        val shipmentId = ""
        val timestamp = 0L
        val otherInfo = ""

        // Act
        val updateData = UpdateData(updateType, shipmentId, timestamp, otherInfo)

        // Assert
        assertEquals(updateType, updateData.getUpdateType())
        assertEquals(shipmentId, updateData.getShipmentId())
        assertEquals(timestamp, updateData.getTimestamp())
        assertEquals(otherInfo, updateData.getOtherInfo())
    }
}