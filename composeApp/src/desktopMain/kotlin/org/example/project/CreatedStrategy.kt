package org.example.project

class CreatedStrategy : AbstractUpdateStrategy("CREATED") {
    
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // For CREATED updates, the shipment should already have the CREATED status
        // We might need to set initial values or just confirm the status
        shipment.updateStatus(ShipmentStatus.CREATED)
        
        // Set expected delivery date if provided in otherInfo
        updateData.getOtherInfo()?.let { otherInfo ->
            try {
                val timestamp = otherInfo.toLong()
                shipment.updateExpectedDeliveryDateTimestamp(timestamp)
            } catch (e: NumberFormatException) {
                // If otherInfo is not a valid timestamp, ignore it for CREATED updates
                println("DEBUG: CreatedStrategy - Invalid timestamp in otherInfo: $otherInfo")
            }
        }
    }
    
    override fun validateUpdate(updateData: UpdateData): Boolean {
        super.validateUpdate(updateData)
        
        // Additional validation for CREATED updates
        if (updateData.getUpdateType() != "CREATED") {
            throw IllegalArgumentException("Invalid update type for CreatedStrategy: ${updateData.getUpdateType()}")
        }
        
        return true
    }
}