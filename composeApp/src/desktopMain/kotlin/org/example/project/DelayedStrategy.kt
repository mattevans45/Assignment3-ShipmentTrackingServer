package org.example.project

class DelayedStrategy : AbstractUpdateStrategy("DELAYED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        shipment.updateStatus(ShipmentStatus.DELAYED)
        val newDeliveryTimestamp = updateData.getOtherInfo()

        if (!newDeliveryTimestamp.isNullOrEmpty()) {
            try {
                val deliveryDate = newDeliveryTimestamp.toLong()
                shipment.setExpectedDeliveryDate(deliveryDate)

            } catch (e: NumberFormatException) {
                println("ERROR: DelayedStrategy - Invalid delivery date format: $newDeliveryTimestamp")
                shipment.addNote("Shipment delayed. Invalid delivery date provided: $newDeliveryTimestamp")
            }
        } else {
            shipment.addNote("Shipment delayed. No new delivery date specified.")
        }
    }

    override fun validateUpdate(updateData: UpdateData) {
        if (updateData.getOtherInfo().isNullOrBlank()) {
            throw IllegalArgumentException("Delayed update requires non-empty otherInfo for expected delivery date")
        }
    }
}

