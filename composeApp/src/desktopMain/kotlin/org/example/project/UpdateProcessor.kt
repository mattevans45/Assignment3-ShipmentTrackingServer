package org.example.project

class UpdateProcessor {
    private val strategyFactory: UpdateStrategyFactory = UpdateStrategyFactory()

    fun processUpdate(updateLine: String) {
        try {
            val updateData = parseUpdateLine(updateLine)

            val strategy = strategyFactory.createStrategy(updateData.getUpdateType())

            strategy.execute(updateData)
            
        } catch (e: Exception) {
            println("ERROR: UpdateProcessor - Failed to process update: ${e.message}")
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
}