package org.example.project

class DelayedStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        shipment ?: return
        shipment.status = ShipmentStatus.DELAYED
        shipment.expectedDeliveryDate = updateData.getOtherInfo()?.toLongOrNull()
    }
}
