package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentStatus
import org.example.project.model.UpdateData

class ShippedStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        shipment ?: return
        val newExpectedDelivery = updateData.getOtherInfo()?.toLongOrNull() ?: return

        shipment.expectedDeliveryDate = newExpectedDelivery
        shipment.status = ShipmentStatus.SHIPPED

        if (shipment.isViolationRelevant()) {
            shipment.validateExpectedDelivery()?.let { violationMessage ->
                shipment.recordViolation(violationMessage)
            }
        }
    }
}