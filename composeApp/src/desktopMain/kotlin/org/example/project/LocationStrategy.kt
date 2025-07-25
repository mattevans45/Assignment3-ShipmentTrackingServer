package org.example.project

class LocationStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        shipment ?: return
        shipment.currentLocation = updateData.getOtherInfo()
    }
}