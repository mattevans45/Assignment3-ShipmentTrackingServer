package org.example.project

import kotlinx.serialization.Serializable

@Serializable
class StandardShipment(
        override val id: String,
        override var status: ShipmentStatus,
        override val createdTimestamp: Long,
        override var expectedDeliveryDate: Long? = null,
        override var currentLocation: String? = null,
        override var updateHistory: List<ShippingUpdate> = listOf(),
        override var notes: List<String> = listOf()
) : Shipment() {
    override fun validateExpectedDelivery(): String? = null
    override fun isViolationRelevant(): Boolean = false
}
