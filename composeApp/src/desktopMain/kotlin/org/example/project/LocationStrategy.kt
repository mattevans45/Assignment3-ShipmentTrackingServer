package org.example.project

class LocationStrategy : AbstractUpdateStrategy("LOCATION") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        updateData.getOtherInfo()?.let { location ->
            shipment.setCurrentLocation(location.trim())
        }
    }
    override fun validateUpdate(updateData: UpdateData) {
        if (updateData.getOtherInfo().isNullOrBlank()) {
            throw IllegalArgumentException("Location update requires non-empty otherInfo")
        }
    }

}