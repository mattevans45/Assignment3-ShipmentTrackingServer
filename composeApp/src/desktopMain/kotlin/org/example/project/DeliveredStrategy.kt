package org.example.project

class DeliveredStrategy : AbstractUpdateStrategy("DELIVERED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        if (shipment.status == ShipmentStatus.CANCELED) return
        shipment.updateStatus(ShipmentStatus.DELIVERED)
    }

    override fun validateUpdate(updateData: UpdateData): Boolean {
        super.validateUpdate(updateData)
        return true
    }
}