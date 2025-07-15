package org.example.project

class NoteAddedStrategy : AbstractUpdateStrategy("NOTEADDED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        updateData.getOtherInfo()?.let { otherInfo ->
            val note = otherInfo.trim()
            if (note.isNotBlank()) {
                shipment.addNote(note)
            }
        }
    }

    override fun validateUpdate(updateData: UpdateData) {
        if (updateData.getOtherInfo().isNullOrBlank()) {
            throw IllegalArgumentException("Note update requires non-empty otherInfo")
        }
    }
}