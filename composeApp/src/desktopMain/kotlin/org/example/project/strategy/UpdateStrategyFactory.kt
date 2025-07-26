package org.example.project.strategy

object UpdateStrategyFactory {
    private val strategies = mapOf<String, AbstractUpdateStrategy>(
        "CREATED" to CreatedStrategy(),
        "SHIPPED" to ShippedStrategy(),
        "LOCATION" to LocationStrategy(),
        "DELIVERED" to DeliveredStrategy(),
        "DELAYED" to DelayedStrategy(),
        "CANCELED" to CanceledStrategy(),
        "LOST" to LostStrategy(),
        "NOTEADDED" to NoteAddedStrategy()
    )
    fun create(updateType: String): AbstractUpdateStrategy? = strategies[updateType.uppercase()]
}