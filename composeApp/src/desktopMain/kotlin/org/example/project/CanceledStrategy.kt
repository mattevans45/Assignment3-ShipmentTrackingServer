package org.example.project

class CanceledStrategy : AbstractUpdateStrategy("CANCELED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        if (shipment.getStatus() != ShipmentStatus.DELIVERED) {
            shipment.updateStatus(ShipmentStatus.CANCELED)
        }
    }
}