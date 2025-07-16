package org.example.project

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.kotlin.*

class UpdateProcessorTest {
    private lateinit var updateProcessor: UpdateProcessor
    private lateinit var mockStrategyFactory: UpdateStrategyFactory
    private lateinit var mockStrategy: UpdateStrategy
    
    @BeforeTest
    fun setUp() {
        mockStrategyFactory = mock()
        mockStrategy = mock()
        
        updateProcessor = UpdateProcessor()
        setPrivateField(updateProcessor, "strategyFactory", mockStrategyFactory)
    }
    
    @Test
    fun processUpdateParsesUpdateLineAndExecutesStrategy() {
        // Arrange
        val updateLine = "CREATED,123,456"
        whenever(mockStrategyFactory.createStrategy("CREATED")).thenReturn(mockStrategy)
        
        // Create a capture for the UpdateData
        val updateDataCaptor = argumentCaptor<UpdateData>()
        
        // Act
        updateProcessor.processUpdate(updateLine)
        
        // Assert
        verify(mockStrategyFactory).createStrategy("CREATED")
        verify(mockStrategy).execute(updateDataCaptor.capture())
        
        val capturedData = updateDataCaptor.firstValue
        assertEquals("CREATED", capturedData.getUpdateType())
        assertEquals("123", capturedData.getShipmentId())
        assertEquals(456L, capturedData.getTimestamp())
    }
    
    @Test
    fun processUpdateHandlesMalformedUpdateLinesGracefully() {
        // Arrange
        val updateLine = "INVALID"
        
        // Act
        updateProcessor.processUpdate(updateLine)
        
        // Assert
        verify(mockStrategyFactory, never()).createStrategy(any())
        verify(mockStrategy, never()).execute(any())
    }
    
    @Test
    fun processUpdateHandlesExceptionsFromStrategyExecution() {
        // Arrange
        val updateLine = "CREATED,123,456"
        whenever(mockStrategyFactory.createStrategy("CREATED")).thenReturn(mockStrategy)
        whenever(mockStrategy.execute(any())).thenThrow(RuntimeException("Test exception"))
        
        // Act - This should not throw an exception
        updateProcessor.processUpdate(updateLine)
        
        // Assert - We're mainly verifying it doesn't crash
        verify(mockStrategyFactory).createStrategy("CREATED")
        verify(mockStrategy).execute(any())
    }
    
    // Utility method to set private fields via reflection
    private fun setPrivateField(instance: Any, fieldName: String, value: Any) {
        val field = instance.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(instance, value)
    }
}