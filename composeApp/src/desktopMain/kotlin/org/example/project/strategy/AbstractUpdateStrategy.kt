package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentStatus
import org.example.project.model.ShippingUpdate
import org.example.project.model.UpdateData
import org.example.project.server.TrackingServer


abstract class AbstractUpdateStrategy {
    fun execute(updateData: UpdateData) {
        validateUpdate(updateData)
        val shipment = TrackingServer.getShipment(updateData.getShipmentId())
        
        if (shipment == null) {
            if (this is CreatedStrategy) {
                processUpdate(null, updateData)
            } else {
                println("ERROR: Shipment ${updateData.getShipmentId()} not found for a non-creation update.")
            }
            return
        }

        val previousStatus = shipment.status
        processUpdate(shipment, updateData)
        val shippingUpdate = createShippingUpdate(shipment, previousStatus, updateData)
        shipment.addUpdate(shippingUpdate)
        TrackingServer.updateShipment(shipment)
    }

    protected abstract fun processUpdate(shipment: Shipment?, updateData: UpdateData)
    open fun validateUpdate(updateData: UpdateData) {
        if (!updateData.isValid()) throw IllegalArgumentException("Update data is invalid: $updateData")
    }
    private fun createShippingUpdate(shipment: Shipment, previousStatus: ShipmentStatus, updateData: UpdateData): ShippingUpdate {
        return ShippingUpdate(
            previousStatus.toString(),
            shipment.status.toString(),
            updateData.getTimestamp(),
            shipment.currentLocation,
            if (updateData.getUpdateType() == "NOTEADDED") updateData.getOtherInfo() else null
        )
    }
}