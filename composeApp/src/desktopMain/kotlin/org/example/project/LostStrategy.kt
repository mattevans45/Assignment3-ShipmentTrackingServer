package org.example.project

class LostStrategy : AbstractUpdateStrategy("LOST") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Business logic: Only mark as lost if not already delivered
        if (shipment.getStatus() != ShipmentStatus.DELIVERED) {
            shipment.updateStatus(ShipmentStatus.LOST)
            
            // Update last known location if provided
            updateData.getOtherInfo()?.let { otherInfo ->
                val lastKnownLocation = otherInfo.trim()
                if (lastKnownLocation.isNotBlank()) {
                    shipment.setCurrentLocation(lastKnownLocation)
                    println("DEBUG: Set last known location: $lastKnownLocation")
                }
            }
        } else {
            println("DEBUG: Cannot mark shipment ${shipment.getId()} as lost - already delivered")
        }
    }
    
    // Override to provide custom notes for lost shipments
    override fun createUpdateNotes(updateData: UpdateData): String? {
        return updateData.getOtherInfo()?.let { location ->
            "Shipment lost. Last known location: $location"
        } ?: "Shipment lost"
    }
}