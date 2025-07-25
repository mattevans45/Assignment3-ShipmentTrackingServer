package org.example.project

import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Serializable
class ExpressShipment(
        override val id: String,
        override var status: ShipmentStatus,
        override val createdTimestamp: Long,
        override var expectedDeliveryDate: Long? = null,
        override var currentLocation: String? = null,
        override var updateHistory: List<ShippingUpdate> = listOf(),
        override var notes: List<String> = listOf()
) : Shipment() {
    override fun validateExpectedDelivery(): String? {
        val threeDays = TimeUnit.DAYS.toMillis(3)
        return if (expectedDeliveryDate != null && expectedDeliveryDate!! > createdTimestamp + threeDays) {
            "An express shipment was updated to include a delivery date more than 3 days after it was created."
        } else null
    }

    override fun isViolationRelevant(): Boolean {
        return status != ShipmentStatus.DELAYED
    }
}