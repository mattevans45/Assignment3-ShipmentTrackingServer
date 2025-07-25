package org.example.project



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