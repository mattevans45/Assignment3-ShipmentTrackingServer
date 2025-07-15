//package org.example.project
//
//class NoteAddedStrategy : AbstractUpdateStrategy("NOTE_ADDED") {
//    override fun execute(shipment: Shipment, updateData: UpdateData) {
//        updateData.getOtherInfo()?.let {
//            val note = parseNote(it)
//            shipment.addNote(note)
//        }
//        shipment.addUpdate(createShippingUpdate(updateData))
//    }
//
//    private fun parseNote(otherInfo: String): String {
//        return otherInfo.trim()
//    }
//}

package org.example.project

class NoteAddedStrategy : AbstractUpdateStrategy("NOTEADDED") {
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Business logic: Add note from otherInfo
        updateData.getOtherInfo()?.let { otherInfo ->
            val note = otherInfo.trim()
            if (note.isNotBlank()) {
                shipment.addNote(note)
                println("DEBUG: Added note to shipment ${shipment.getId()}: $note")
            }
        }
    }
    
    // Override to provide custom notes for note addition
    override fun createUpdateNotes(updateData: UpdateData): String? {
        return updateData.getOtherInfo()?.let { note ->
            "Note added: $note"
        }
    }
}