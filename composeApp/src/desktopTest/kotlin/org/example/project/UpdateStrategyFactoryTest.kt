package org.example.project

import org.example.project.strategy.UpdateStrategyFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UpdateStrategyFactoryTest {

    @Test
    fun createReturnsCreatedStrategyForCreatedType() {
        val strategy = UpdateStrategyFactory.create("CREATED")
        assertNotNull(strategy)
        assertEquals("CreatedStrategy", strategy::class.simpleName)
    }

    @Test
    fun createReturnsCanceledStrategyForCanceledType() {
        val strategy = UpdateStrategyFactory.create("CANCELED")
        assertNotNull(strategy)
        assertEquals("CanceledStrategy", strategy::class.simpleName)
    }

    @Test
    fun createReturnsShippedStrategyForShippedType() {
        val strategy = UpdateStrategyFactory.create("SHIPPED")
        assertNotNull(strategy)
        assertEquals("ShippedStrategy", strategy::class.simpleName)
    }

    @Test
    fun createReturnsLocationStrategyForLocationType() {
        val strategy = UpdateStrategyFactory.create("LOCATION")
        assertNotNull(strategy)
        assertEquals("LocationStrategy", strategy::class.simpleName)
    }

    @Test
    fun createReturnsDelayedStrategyForDelayedType() {
        val strategy = UpdateStrategyFactory.create("DELAYED")
        assertNotNull(strategy)
        assertEquals("DelayedStrategy", strategy::class.simpleName)
    }

    @Test
    fun createReturnsDeliveredStrategyForDeliveredType() {
        val strategy = UpdateStrategyFactory.create("DELIVERED")
        assertNotNull(strategy)
        assertEquals("DeliveredStrategy", strategy::class.simpleName)
    }

    @Test
    fun createReturnsLostStrategyForLostType() {
        val strategy = UpdateStrategyFactory.create("LOST")
        assertNotNull(strategy)
        assertEquals("LostStrategy", strategy::class.simpleName)
    }

    @Test
    fun createReturnsNoteAddedStrategyForNoteAddedType() {
        val strategy = UpdateStrategyFactory.create("NOTEADDED")
        assertNotNull(strategy)
        assertEquals("NoteAddedStrategy", strategy::class.simpleName)
    }

    @Test
    fun createReturnsNullForUnknownType() {
        val strategy = UpdateStrategyFactory.create("UNKNOWN")
        assertNull(strategy)
    }

    @Test
    fun createHandlesCaseInsensitiveInput() {
        val strategy = UpdateStrategyFactory.create("created")
        assertNotNull(strategy)
        assertEquals("CreatedStrategy", strategy::class.simpleName)
    }
}