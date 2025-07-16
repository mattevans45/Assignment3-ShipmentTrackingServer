package org.example.project

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith

class UpdateStrategyFactoryTest {

    @Test
    fun createStrategyReturnsCreatedStrategyForCreatedType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("CREATED")

        // Assert
        assertNotNull(strategy)
        assertEquals("CreatedStrategy", strategy::class.simpleName)
    }

    @Test
    fun createStrategyReturnsCanceledStrategyForCanceledType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("CANCELED")

        // Assert
        assertNotNull(strategy)
        assertEquals("CanceledStrategy", strategy::class.simpleName)
    }

    @Test
    fun createStrategyReturnsShippedStrategyForShippedType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("SHIPPED")

        // Assert
        assertNotNull(strategy)
        assertEquals("ShippedStrategy", strategy::class.simpleName)
    }

    @Test
    fun createStrategyReturnsLocationStrategyForLocationType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("LOCATION")

        // Assert
        assertNotNull(strategy)
        assertEquals("LocationStrategy", strategy::class.simpleName)
    }

    @Test
    fun createStrategyReturnsDelayedStrategyForDelayedType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("DELAYED")

        // Assert
        assertNotNull(strategy)
        assertEquals("DelayedStrategy", strategy::class.simpleName)
    }

    @Test
    fun createStrategyReturnsDeliveredStrategyForDeliveredType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("DELIVERED")

        // Assert
        assertNotNull(strategy)
        assertEquals("DeliveredStrategy", strategy::class.simpleName)
    }

    @Test
    fun createStrategyReturnsLostStrategyForLostType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("LOST")

        // Assert
        assertNotNull(strategy)
        assertEquals("LostStrategy", strategy::class.simpleName)
    }

    @Test
    fun createStrategyReturnsNoteAddedStrategyForNoteAddedType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("NOTEADDED")

        // Assert
        assertNotNull(strategy)
        assertEquals("NoteAddedStrategy", strategy::class.simpleName)
    }

    @Test
    fun createStrategyThrowsExceptionForUnknownType() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act & Assert
        assertFailsWith<IllegalArgumentException> {
            factory.createStrategy("UNKNOWN")
        }
    }

    @Test
    fun createStrategyHandlesCaseInsensitiveInput() {
        // Arrange
        val factory = UpdateStrategyFactory()

        // Act
        val strategy = factory.createStrategy("created")

        // Assert
        assertNotNull(strategy)
        assertEquals("CreatedStrategy", strategy::class.simpleName)
    }
}