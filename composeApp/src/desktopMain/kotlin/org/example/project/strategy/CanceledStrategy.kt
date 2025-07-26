package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.ShipmentStatus
import org.example.project.model.UpdateData

class CanceledStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        shipment ?: return
        shipment.status = ShipmentStatus.CANCELED
    }
}