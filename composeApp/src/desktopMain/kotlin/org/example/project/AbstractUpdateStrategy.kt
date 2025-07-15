package org.example.project

import kotlinx.coroutines.*

abstract class AbstractUpdateStrategy(private val updateType: String) : UpdateStrategy {
    protected val simulator: TrackingSimulator = TrackingSimulator.getInstance()

    override fun validateUpdate(updateData: UpdateData) {
        if (updateData.getUpdateType() != updateType) {
            throw IllegalArgumentException("Invalid update type: ${updateData.getUpdateType()}")
        }
    }

    override fun getUpdateType(): String = updateType
    
    protected suspend fun getOrCreateShipment(updateData: UpdateData): Shipment {
        val existingShipment = simulator.getShipment(updateData.getShipmentId())
        
        if (existingShipment != null) {
            return existingShipment
        }
        
        val newShipment = Shipment(
            id = updateData.getShipmentId(),
            status = ShipmentStatus.CREATED,
            createdAt = updateData.getTimestamp()
        )
        
        simulator.addShipment(newShipment)
        return newShipment
    }
    
    // Template method - handles the common workflow
    override suspend fun execute(updateData: UpdateData) {
        println("DEBUG: ${this.javaClass.simpleName} executing for ${updateData.getShipmentId()}")
        
        validateUpdate(updateData)
        val shipment = getOrCreateShipment(updateData)
        val previousStatus = shipment.getStatus()
        
        // Let strategy handle the specific logic
        processUpdate(shipment, updateData)
        
        // Create and add the update record here (single responsibility)
        val shippingUpdate = createShippingUpdate(shipment, previousStatus, updateData)
        shipment.addUpdate(shippingUpdate)
        
        println("DEBUG: ${this.javaClass.simpleName} completed for ${shipment.getId()}")
    }
    
    // Each strategy implements this - only business logic, no update creation
    protected abstract fun processUpdate(shipment: Shipment, updateData: UpdateData)
    
    // Helper method to create shipping updates
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
    
    // Override this in strategies that need custom notes
    protected open fun createUpdateNotes(updateData: UpdateData): String? {
        return when (updateType) {
            "NOTEADDED" -> "Note added: ${updateData.getOtherInfo()}"
            "LOCATION" -> "Location updated to: ${updateData.getOtherInfo()}"
            "CANCELED" -> "Cancellation reason: ${updateData.getOtherInfo()}"
            else -> null
        }
    }
}
