package org.example.project

import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Serializable
class OvernightShipment(
        override val id: String,
        override var status: ShipmentStatus,
        override val createdTimestamp: Long,
        override var expectedDeliveryDate: Long? = null,
        override var currentLocation: String? = null,
        override var updateHistory: List<ShippingUpdate> = listOf(),
        override var notes: List<String> = listOf()
) : Shipment() {
    override fun validateExpectedDelivery(): String? {
        val oneDay = TimeUnit.DAYS.toMillis(1)
        return if (expectedDeliveryDate != null && expectedDeliveryDate!! > createdTimestamp + oneDay) {
            "An overnight shipment was updated to include a delivery date later than 24 hours after it was created."
        } else null
    }

    override fun isViolationRelevant(): Boolean {
        return status != ShipmentStatus.DELAYED
    }
}