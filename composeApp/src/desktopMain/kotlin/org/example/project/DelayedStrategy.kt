package org.example.project

class DelayedStrategy : AbstractUpdateStrategy("DELAYED") {
    
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Do not delay if already delivered
        if (shipment.status == ShipmentStatus.DELIVERED) return

        shipment.updateStatus(ShipmentStatus.DELAYED)
        
        // Parse the new delivery date from otherInfo
        updateData.getOtherInfo()?.let { otherInfo ->
            val newDeliveryDate = parseNewDeliveryDate(otherInfo)
            shipment.updateExpectedDeliveryDateTimestamp(newDeliveryDate)
        }
    }
    
    override fun validateUpdate(updateData: UpdateData): Boolean {
        super.validateUpdate(updateData)
        
        // Validate that we have delivery date info
        if (updateData.getOtherInfo().isNullOrBlank()) {
            throw IllegalArgumentException("Delayed update must include new delivery date")
        }
        
        return true
    }
    
    private fun parseNewDeliveryDate(otherInfo: String): Long {
        return try {
            otherInfo.toLong()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid delivery date format: $otherInfo")
        }
    }
}

