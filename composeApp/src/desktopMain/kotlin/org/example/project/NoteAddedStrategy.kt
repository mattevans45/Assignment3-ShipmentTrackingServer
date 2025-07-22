package org.example.project

class NoteAddedStrategy : AbstractUpdateStrategy("NOTEADDED") {
    
    override fun processUpdate(shipment: Shipment, updateData: UpdateData) {
        // Add note without changing status
        updateData.getOtherInfo()?.let { otherInfo ->
            val note = parseNote(otherInfo)
            shipment.addNote(note)
        }
    }
    
    override fun validateUpdate(updateData: UpdateData): Boolean {
        super.validateUpdate(updateData)
        
        if (updateData.getOtherInfo().isNullOrBlank()) {
            throw IllegalArgumentException("Note update must include note text")
        }
        
        return true
    }
    
    private fun parseNote(otherInfo: String): String {
        return otherInfo.trim()
    }
}