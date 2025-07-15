package org.example.project

abstract class AbstractUpdateStrategy(private val updateType: String) : UpdateStrategy {
    protected val simulator: TrackingSimulator = TrackingSimulator.getInstance()
    
    override fun execute(updateData: UpdateData) {
        validateUpdate(updateData)
        
        val shipmentId = updateData.getShipmentId()
        val shipment = simulator.getShipment(shipmentId) 
            ?: handleShipmentNotFound(updateData)
        
        val previousStatus = shipment.getStatus()

        val updatedShipment = shipment.copy()

        processUpdate(updatedShipment, updateData)

        val shippingUpdate = createShippingUpdate(
            updatedShipment, 
            previousStatus,  
            updateData
        )
        updatedShipment.addUpdate(shippingUpdate)
        simulator.updateShipment(updatedShipment)
        simulator.notifyObservers(updatedShipment)
    }
    
    private fun handleShipmentNotFound(updateData: UpdateData): Shipment {
        if (updateType == "CREATED") {
            val newShipment = Shipment(
                id = updateData.getShipmentId(),
                status = ShipmentStatus.CREATED,
                createdTimestamp = updateData.getTimestamp()
            )
            simulator.addShipment(newShipment)
            simulator.notifyShipmentCreated(newShipment)
            return newShipment
        } else {
            simulator.notifyShipmentNotFound(updateData.getShipmentId())
            throw IllegalStateException("Shipment not found: ${updateData.getShipmentId()}")
        }
    }
    
    protected abstract fun processUpdate(shipment: Shipment, updateData: UpdateData)
    
    private fun createShippingUpdate(
        shipment: Shipment, 
        previousStatus: ShipmentStatus, 
        updateData: UpdateData
    ): ShippingUpdate {
        return ShippingUpdate(
            previousStatus = previousStatus.toString(),
            newStatus = shipment.getStatus().toString(),
            timestamp = updateData.getTimestamp(),
            location = shipment.getCurrentLocation(),
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


    override fun validateUpdate(updateData: UpdateData) {
        if (updateData.getShipmentId().isBlank()) {
            throw IllegalArgumentException("Shipment ID cannot be blank")
        }
        if (updateData.getTimestamp() <= 0) {
            throw IllegalArgumentException("Invalid timestamp")
        }
    }
}
