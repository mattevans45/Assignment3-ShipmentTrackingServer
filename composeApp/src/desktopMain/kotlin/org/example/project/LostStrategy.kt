package org.example.project

class LostStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        shipment ?: return
        shipment.status = ShipmentStatus.LOST
    }
}