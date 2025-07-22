package org.example.project

abstract class AbstractUpdateStrategy(protected val updateType: String) {
    protected val simulator: TrackingSimulator get() = TrackingSimulator.getInstance()

    fun execute(updateData: UpdateData) {
        validateUpdate(updateData)

        val shipmentId = updateData.getShipmentId()
        val shipment = simulator.getShipment(shipmentId)
            ?: return handleShipmentNotFound(updateData)

        val previousStatus = shipment.status

        // Work with the original shipment (not a copy)
        processUpdate(shipment, updateData)

        val shippingUpdate = createShippingUpdate(
            shipment,
            previousStatus,
            updateData
        )
        shipment.addUpdate(shippingUpdate) // This will trigger notifyObservers()

        simulator.updateShipment(shipment)
    }

    private fun handleShipmentNotFound(updateData: UpdateData) {
        if (updateType == "CREATED") {
            val newShipment = Shipment(
                id = updateData.getShipmentId(),
                status = ShipmentStatus.CREATED,
                createdTimestamp = updateData.getTimestamp()
            )
            simulator.addShipment(newShipment)
        } else {
            throw IllegalStateException("Shipment not found: ${updateData.getShipmentId()}")
        }
    }

    protected abstract fun processUpdate(shipment: Shipment, updateData: UpdateData)

    protected fun createShippingUpdate(
        shipment: Shipment,
        previousStatus: ShipmentStatus,
        updateData: UpdateData
    ): ShippingUpdate {
        return ShippingUpdate(
            previousStatus = previousStatus.toString(),
            newStatus = shipment.status.toString(),
            timestamp = updateData.getTimestamp(),
            location = shipment.currentLocation?.toString(),
            notes = createUpdateNotes(updateData)
        )
    }

    protected open fun createUpdateNotes(updateData: UpdateData): String? {
        return when (updateType) {
            "NOTEADDED" -> "Note added: ${updateData.getOtherInfo()}"
            "LOCATION" -> "Location updated to: ${updateData.getOtherInfo()}"
            "CANCELED" -> "Cancellation reason: ${updateData.getOtherInfo()}"
            "DELAYED" -> "Delayed until: ${updateData.getOtherInfo()?.let { formatTimestamp(it.toLong()) } ?: "Unknown"}"
            else -> null
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val instant = java.time.Instant.ofEpochMilli(timestamp)
        val dateTime = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    open fun validateUpdate(updateData: UpdateData): Boolean {
        if (updateData.getShipmentId().isBlank()) {
            throw IllegalArgumentException("Shipment ID cannot be blank")
        }
        if (updateData.getTimestamp() <= 0) {
            throw IllegalArgumentException("Invalid timestamp")
        }
        return true
    }



    // Helper methods for validation
    protected fun validateTimestamp(timestamp: Long): Boolean = timestamp > 0

    protected fun validateShipmentId(shipmentId: String): Boolean = shipmentId.isNotBlank()
}