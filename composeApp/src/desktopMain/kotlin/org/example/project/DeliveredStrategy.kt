package org.example.project

class DeliveredStrategy : AbstractUpdateStrategy("DELIVERED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        shipment.updateStatus(ShipmentStatus.DELIVERED)
    }
}