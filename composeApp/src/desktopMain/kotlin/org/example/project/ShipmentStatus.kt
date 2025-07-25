package org.example.project

import kotlinx.serialization.Serializable

@Serializable
enum class ShipmentStatus {
    CREATED,
    SHIPPED,
    DELIVERED,
    DELAYED,
    LOST,
    CANCELED,
    EXCEPTION
}