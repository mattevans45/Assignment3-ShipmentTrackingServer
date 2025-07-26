package org.example.project.server

import org.example.project.model.ShipmentUpdatePayload
import org.example.project.model.UpdateData
import org.example.project.strategy.UpdateStrategyFactory

class UpdateProcessor(
    private val strategyFactory: UpdateStrategyFactory = UpdateStrategyFactory
) {
    fun process(payload: ShipmentUpdatePayload) {
        try {
            val updateData = UpdateData(
                payload.updateType,
                payload.shipmentId,
                payload.timestamp,
                payload.otherInfo
            )
            strategyFactory.create(updateData.getUpdateType())?.execute(updateData)
                ?: println("Warning: No strategy for '${updateData.getUpdateType()}'")
        } catch (e: Exception) {
            println("ERROR: UpdateProcessor failed: ${e.message}")
        }
    }
}