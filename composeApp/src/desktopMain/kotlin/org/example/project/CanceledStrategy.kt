package org.example.project

class CanceledStrategy : AbstractUpdateStrategy("CANCELED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Business logic: Only cancel if not already delivered
        if (shipment.getStatus() != ShipmentStatus.DELIVERED) {
            shipment.updateStatus(ShipmentStatus.CANCELED)
            
            // Add cancellation reason if provided
            updateData.getOtherInfo()?.let { otherInfo ->
                val cancellationReason = otherInfo.trim()
                if (cancellationReason.isNotBlank()) {
                    shipment.addNote("Cancellation reason: $cancellationReason")
                    println("DEBUG: Added cancellation reason: $cancellationReason")
                }
            }
        } else {
            println("DEBUG: Cannot cancel shipment ${shipment.getId()} - already delivered")
        }
    }
    
    // Override to provide custom notes for cancellation
    override fun createUpdateNotes(updateData: UpdateData): String? {
        return updateData.getOtherInfo()?.let { reason ->
            "Shipment canceled. Reason: $reason"
        } ?: "Shipment canceled"
    }
}