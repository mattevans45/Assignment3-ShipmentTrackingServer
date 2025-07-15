package org.example.project

class DeliveredStrategy : AbstractUpdateStrategy("DELIVERED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Business logic: Always allow delivery (can deliver from any state)
        shipment.updateStatus(ShipmentStatus.DELIVERED)
        
        // Set final delivery location if provided
        updateData.getOtherInfo()?.let { otherInfo ->
            val deliveryLocation = otherInfo.trim()
            if (deliveryLocation.isNotBlank()) {
                shipment.setCurrentLocation(deliveryLocation)
                println("DEBUG: Set final delivery location: $deliveryLocation")
            }
        }
    }
}