package org.example.project

class UpdateProcessor {

    fun process(payload: ShipmentUpdatePayload) {
        try {
            val updateData = UpdateData(
                payload.updateType,
                payload.shipmentId,
                payload.timestamp,
                payload.otherInfo
            )
            UpdateStrategyFactory.create(updateData.getUpdateType())?.execute(updateData)
                ?: println("Warning: No strategy for '${updateData.getUpdateType()}'")
        } catch (e: Exception) {
            println("ERROR: UpdateProcessor failed: ${e.message}")
        }
    }

}