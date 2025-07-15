//package org.example.project
//
//class ShippedStrategy : AbstractUpdateStrategy("SHIPPED") {
//    override fun execute(shipment: Shipment, updateData: UpdateData) {
//        shipment.setStatus(ShipmentStatus.SHIPPED)
//        updateData.getOtherInfo()?.let {
//            val deliveryDate = parseExpectedDeliveryDate(it)
//            shipment.setExpectedDeliveryDateTimestamp(deliveryDate)
//        }
//        shipment.addUpdate(createShippingUpdate(updateData))
//    }
//
//    private fun parseExpectedDeliveryDate(otherInfo: String): Long {
//        return try {
//            otherInfo.toLong()
//        } catch (e: NumberFormatException) {
//            System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L) // Default 7 days
//        }
//    }
//}

package org.example.project

class ShippedStrategy : AbstractUpdateStrategy("SHIPPED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Business logic: Only update if not in terminal states
        if (shipment.getStatus() !in listOf(ShipmentStatus.DELIVERED, ShipmentStatus.CANCELED, ShipmentStatus.LOST)) {
            shipment.updateStatus(ShipmentStatus.SHIPPED)
            
            // Set expected delivery date
            updateData.getOtherInfo()?.toLongOrNull()?.let { deliveryDate ->
                shipment.setExpectedDeliveryDate(deliveryDate)
            } ?: run {
                // Default 7 days from now
                val defaultDeliveryDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
                shipment.setExpectedDeliveryDate(defaultDeliveryDate)
            }
        }
    }
}