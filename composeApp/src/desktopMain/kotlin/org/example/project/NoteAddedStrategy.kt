package org.example.project

class NoteAddedStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        shipment ?: return
        updateData.getOtherInfo()?.let { shipment.addNote(it) }
    }
}
