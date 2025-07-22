package org.example.project

class LocationStrategy : AbstractUpdateStrategy("LOCATION") {
    
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Update location without changing status
        updateData.getOtherInfo()?.let { otherInfo ->
            val location = parseLocation(otherInfo)
            shipment.updateCurrentLocation(location)
        }
    }
    
    override fun validateUpdate(updateData: UpdateData): Boolean {
        super.validateUpdate(updateData)
        
        if (updateData.getOtherInfo().isNullOrBlank()) {
            throw IllegalArgumentException("Location update must include location information")
        }
        
        return true
    }
    
    private fun parseLocation(otherInfo: String): String {
        return otherInfo.trim()
    }
}