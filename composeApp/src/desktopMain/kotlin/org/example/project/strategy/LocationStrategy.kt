package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.UpdateData

class LocationStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        shipment ?: return
        shipment.currentLocation = updateData.getOtherInfo()
    }
}