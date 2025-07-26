package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
enum class ShipmentStatus {
    CREATED,
    SHIPPED,
    DELIVERED,
    DELAYED,
    LOST,
    CANCELED
}