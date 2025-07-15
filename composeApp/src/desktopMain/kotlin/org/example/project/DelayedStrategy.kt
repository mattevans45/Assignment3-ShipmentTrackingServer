package org.example.project

class DelayedStrategy : AbstractUpdateStrategy("DELAYED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Business logic: Only delay if not already delivered
        if (shipment.getStatus() != ShipmentStatus.DELIVERED) {
            shipment.updateStatus(ShipmentStatus.DELAYED)
            
            // Update expected delivery date from otherInfo or set default
            updateData.getOtherInfo()?.let { otherInfo ->
                try {
                    val newDeliveryDate = otherInfo.toLong()
                    shipment.setExpectedDeliveryDate(newDeliveryDate)
                    println("DEBUG: Updated delivery date due to delay: $newDeliveryDate")
                } catch (e: NumberFormatException) {
                    setDefaultDelayedDeliveryDate(shipment)
                }
            } ?: run {
                setDefaultDelayedDeliveryDate(shipment)
            }
        } else {
            println("DEBUG: Cannot delay shipment ${shipment.getId()} - already delivered")
        }
    }
    
    private fun setDefaultDelayedDeliveryDate(shipment: Shipment) {
        val defaultDeliveryDate = System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L) // 14 days
        shipment.setExpectedDeliveryDate(defaultDeliveryDate)
        println("DEBUG: Set default delayed delivery date: $defaultDeliveryDate")
    }
}

