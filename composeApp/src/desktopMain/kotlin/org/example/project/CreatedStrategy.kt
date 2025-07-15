package org.example.project

class CreatedStrategy : AbstractUpdateStrategy("CREATED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        shipment.updateStatus(ShipmentStatus.CREATED)
        updateData.getOtherInfo()?.toLongOrNull()?.let { deliveryDate ->
            shipment.setExpectedDeliveryDate(deliveryDate)
        }
    }
}