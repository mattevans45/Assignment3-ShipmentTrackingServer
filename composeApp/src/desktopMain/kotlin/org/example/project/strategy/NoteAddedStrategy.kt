package org.example.project.strategy

import org.example.project.model.Shipment
import org.example.project.model.UpdateData

class NoteAddedStrategy : AbstractUpdateStrategy() {
    override fun processUpdate(shipment: Shipment?, updateData: UpdateData) {
        shipment ?: return
        updateData.getOtherInfo()?.let { shipment.addNote(it) }
    }
}
