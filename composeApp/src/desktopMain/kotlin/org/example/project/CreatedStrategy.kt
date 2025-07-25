package org.example.project

class CreatedStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        if (shipment != null) {
            println("Shipment ${updateData.getShipmentId()} already exists.")
            return
        }
        val type = try { ShipmentType.valueOf(updateData.getOtherInfo()?.uppercase() ?: "STANDARD") } catch (e: Exception) { ShipmentType.STANDARD }
        val newShipment = ShipmentFactory.create(type, updateData.getShipmentId(), ShipmentStatus.CREATED, updateData.getTimestamp(), null)
        TrackingServer.addShipment(newShipment)
        println("Created ${type.name.lowercase()} shipment: ${updateData.getShipmentId()}")
    }
}
