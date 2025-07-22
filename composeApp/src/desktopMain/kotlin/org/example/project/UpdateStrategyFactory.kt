package org.example.project

class UpdateStrategyFactory {
    fun createStrategy(updateType: String): AbstractUpdateStrategy {
        return when (updateType.uppercase()) {
            "CREATED" -> CreatedStrategy()
            "SHIPPED" -> ShippedStrategy()
            "LOCATION" -> LocationStrategy()
            "DELAYED" -> DelayedStrategy()
            "DELIVERED" -> DeliveredStrategy()
            "CANCELED" -> CanceledStrategy()
            "LOST" -> LostStrategy()
            "NOTEADDED" -> NoteAddedStrategy()
            else -> throw IllegalArgumentException("Unknown update type: $updateType")
        }
    }
}