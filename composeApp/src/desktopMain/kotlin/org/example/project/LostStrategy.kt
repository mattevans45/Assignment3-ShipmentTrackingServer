package org.example.project

class LostStrategy : AbstractUpdateStrategy("LOST") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        if (shipment.status != ShipmentStatus.DELIVERED) {
            shipment.updateStatus(ShipmentStatus.LOST)
        }
    }

}