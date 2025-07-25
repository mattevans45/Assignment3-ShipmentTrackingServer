package org.example.project

enum class ShipmentType {
    STANDARD, EXPRESS, OVERNIGHT, BULK
}

object ShipmentFactory {
    fun create(type: ShipmentType, id: String, status: ShipmentStatus, createdTimestamp: Long, expectedDeliveryDate: Long?): Shipment {
        return when (type) {
            ShipmentType.STANDARD -> StandardShipment(id, status, createdTimestamp, expectedDeliveryDate)
            ShipmentType.EXPRESS -> ExpressShipment(id, status, createdTimestamp, expectedDeliveryDate)
            ShipmentType.OVERNIGHT -> OvernightShipment(id, status, createdTimestamp, expectedDeliveryDate)
            ShipmentType.BULK -> BulkShipment(id, status, createdTimestamp, expectedDeliveryDate)
        }
    }
}