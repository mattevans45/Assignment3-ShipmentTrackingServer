package org.example.project.model


data class UpdateData(
    private val updateType: String,
    private val shipmentId: String,
    private val timestamp: Long,
    private val otherInfo: String? = null
) {
    fun getUpdateType(): String = updateType.uppercase()
    fun getShipmentId(): String = shipmentId
    fun getTimestamp(): Long = timestamp
    fun getOtherInfo(): String? = otherInfo
    fun isValid(): Boolean = updateType.isNotBlank() && shipmentId.isNotBlank() && timestamp > 0
    override fun toString(): String = "UpdateData(type=$updateType, shipmentId=$shipmentId, timestamp=$timestamp, otherInfo=$otherInfo)"
}