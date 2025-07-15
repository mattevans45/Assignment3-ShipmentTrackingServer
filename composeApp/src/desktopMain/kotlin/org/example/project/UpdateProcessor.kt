package org.example.project

import kotlinx.coroutines.*

class UpdateProcessor {
    private val strategyFactory: UpdateStrategyFactory = UpdateStrategyFactory()
    private val simulator: TrackingSimulator = TrackingSimulator.getInstance()

    suspend fun processUpdate(updateLine: String) {
        try {
            println("DEBUG: Processing update line: $updateLine")
            val updateData = parseUpdateLine(updateLine)
            println("DEBUG: Parsed update: ${updateData.getUpdateType()} for ${updateData.getShipmentId()}")
            
            // Validate the update data
            validateUpdateData(updateData)
            
            val strategy = strategyFactory.createStrategy(updateData.getUpdateType())
            println("DEBUG: Using strategy: ${strategy.javaClass.simpleName}")
            
            // Let the strategy handle everything - including shipment creation/retrieval
            strategy.execute(updateData)
            
        } catch (e: Exception) {
            println("ERROR: Failed to process update: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun parseUpdateLine(line: String): UpdateData {
        val parts = line.split(",")
        return UpdateData(
            updateType = parts[0].trim(),
            shipmentId = parts[1].trim(),
            timestamp = parts[2].trim().toLong(),
            otherInfo = if (parts.size > 3) parts[3].trim() else null
        )
    }
    
    private fun validateUpdateData(updateData: UpdateData) {
        if (updateData.getShipmentId().isBlank()) {
            throw IllegalArgumentException("Shipment ID cannot be blank")
        }
        if (updateData.getTimestamp() <= 0) {
            throw IllegalArgumentException("Invalid timestamp")
        }
    }
}