package org.example.project

class CreatedStrategy : AbstractUpdateStrategy("CREATED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Business logic only
        shipment.updateStatus(ShipmentStatus.CREATED)
        
        // Set expected delivery date if provided
        updateData.getOtherInfo()?.toLongOrNull()?.let { deliveryDate ->
            shipment.setExpectedDeliveryDate(deliveryDate)
        }
    }
}