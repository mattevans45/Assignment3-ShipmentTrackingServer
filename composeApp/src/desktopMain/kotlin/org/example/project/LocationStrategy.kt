//package org.example.project
//
//class LocationStrategy : AbstractUpdateStrategy("LOCATION") {
//    override fun execute(shipment: Shipment, updateData: UpdateData) {
//        shipment.setStatus(ShipmentStatus.IN_TRANSIT)
//        updateData.getOtherInfo()?.let {
//            val location = parseLocation(it)
//            shipment.setCurrentLocation(location)
//        }
//        shipment.addUpdate(createShippingUpdate(updateData))
//    }
//
//    private fun parseLocation(otherInfo: String): String {
//        return otherInfo.trim()
//    }
//}

package org.example.project

class LocationStrategy : AbstractUpdateStrategy("LOCATION") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Business logic: Update location from otherInfo
        updateData.getOtherInfo()?.let { location ->
            shipment.setCurrentLocation(location.trim())
        }
    }
}