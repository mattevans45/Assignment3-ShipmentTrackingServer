package org.example.project

class ShippedStrategy : AbstractUpdateStrategy("SHIPPED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        if (shipment.getStatus() !in listOf(ShipmentStatus.DELIVERED, ShipmentStatus.CANCELED, ShipmentStatus.LOST)) {
            shipment.updateStatus(ShipmentStatus.SHIPPED)
            updateData.getOtherInfo()?.toLongOrNull()?.let { deliveryDate ->
                shipment.setExpectedDeliveryDate(deliveryDate)
            }
        }
    }
}