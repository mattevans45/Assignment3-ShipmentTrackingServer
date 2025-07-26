package org.example.project

import org.example.project.model.ShipmentUpdatePayload
import org.example.project.model.UpdateData
import org.example.project.server.UpdateProcessor
import org.example.project.strategy.AbstractUpdateStrategy
import org.example.project.strategy.UpdateStrategyFactory
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*

class UpdateProcessorTest {
    private lateinit var updateProcessor: UpdateProcessor
    private lateinit var mockStrategyFactory: UpdateStrategyFactory
    private lateinit var mockStrategy: AbstractUpdateStrategy

    @BeforeTest
    fun setUp() {
        mockStrategyFactory = mock()
        mockStrategy = mock()
        updateProcessor = UpdateProcessor(mockStrategyFactory)
    }

    @Test
    fun processUpdateParsesPayloadAndExecutesStrategy() {
        // Arrange
        val payload = ShipmentUpdatePayload("CREATED", "123", 456L, null)
        whenever(mockStrategyFactory.create("CREATED")).thenReturn(mockStrategy)

        // Act
        updateProcessor.process(payload)

        // Assert
        verify(mockStrategyFactory).create("CREATED")
        verify(mockStrategy).execute(any())
    }

    @Test
    fun processUpdateHandlesNoStrategy() {
        // Arrange
        val payload = ShipmentUpdatePayload("UNKNOWN", "123", 456L, null)
        whenever(mockStrategyFactory.create("UNKNOWN")).thenReturn(null)

        // Act
        updateProcessor.process(payload)

        // Assert
        verify(mockStrategyFactory).create("UNKNOWN")
        verify(mockStrategy, never()).execute(any())
    }

    @Test
    fun processUpdateHandlesExceptionsFromStrategyExecution() {
        // Arrange
        val payload = ShipmentUpdatePayload("CREATED", "123", 456L, null)
        whenever(mockStrategyFactory.create("CREATED")).thenReturn(mockStrategy)
        whenever(mockStrategy.execute(any())).thenThrow(RuntimeException("Test exception"))

        // Act - Should not throw
        updateProcessor.process(payload)

        // Assert
        verify(mockStrategyFactory).create("CREATED")
        verify(mockStrategy).execute(any())
    }
}