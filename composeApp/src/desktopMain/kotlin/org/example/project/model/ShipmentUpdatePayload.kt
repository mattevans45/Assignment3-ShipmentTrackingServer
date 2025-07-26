package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class ShipmentUpdatePayload(
    val updateType: String,
    val shipmentId: String,
    val timestamp: Long,
    val otherInfo: String? = null
)