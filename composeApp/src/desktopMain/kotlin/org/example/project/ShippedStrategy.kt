package org.example.project

class ShippedStrategy : AbstractUpdateStrategy("SHIPPED") {
    
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        if (shipment.status == ShipmentStatus.CANCELED) return
        if (shipment.status == ShipmentStatus.DELIVERED) {
            throw IllegalStateException("Cannot ship a delivered shipment")
        }
        shipment.updateStatus(ShipmentStatus.SHIPPED)
        
        // Parse expected delivery date from otherInfo
        updateData.getOtherInfo()?.let { otherInfo ->
            val deliveryDate = parseExpectedDeliveryDate(otherInfo)
            shipment.updateExpectedDeliveryDateTimestamp(deliveryDate)
        }
    }
    
    override fun validateUpdate(updateData: UpdateData): Boolean {
        super.validateUpdate(updateData)
        return true
    }
    
    private fun parseExpectedDeliveryDate(otherInfo: String): Long {
        return try {
            otherInfo.toLong()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid delivery date format: $otherInfo")
        }
    }
}