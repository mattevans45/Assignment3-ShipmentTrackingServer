package org.example.project

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.mockito.kotlin.*

class SimulationControllerTest {
    // Test subject
    private lateinit var simulationController: SimulationController
    
    // Mocks
    private lateinit var mockFileProcessor: FileProcessor
    private lateinit var mockUpdateProcessor: UpdateProcessor
    private lateinit var mockSimulator: TrackingSimulator
    
    @BeforeTest
    fun setUp() {
        // Create mocks
        mockFileProcessor = mock()
        mockUpdateProcessor = mock()
        mockSimulator = mock()
        TrackingSimulator.setTestInstance(mockSimulator)
        
        // Create test subject with mocks
        simulationController = SimulationController()
        
        // Use reflection to inject mocks
        setPrivateField(simulationController, "fileProcessor", mockFileProcessor)
        setPrivateField(simulationController, "updateProcessor", mockUpdateProcessor)
    }
    
    @AfterTest
    fun tearDown() {
        TrackingSimulator.resetInstance()
    }
    
    @Test
    fun startSimulationReturnsFalseWhenAlreadyRunning() {
        // Arrange
        setPrivateField(simulationController, "isRunning", true)
        
        // Act
        val result = simulationController.startSimulation()
        
        // Assert
        assertFalse(result)
        verify(mockFileProcessor, never()).getTotalUpdates()
    }
    
    @Test
    fun startSimulationReturnsFalseWhenFileIsEmpty() {
        // Arrange
        setPrivateField(simulationController, "isRunning", false)
        whenever(mockFileProcessor.isEmpty()).thenReturn(true)
        
        // Act
        val result = simulationController.startSimulation()
        
        // Assert
        assertFalse(result)
    }

    @Test
    fun loadFileReturnsFalseForNonexistentFile() {
        // Arrange
        val controller = SimulationController()

        // Act
        val result = controller.loadFile("nonexistent.txt")

        // Assert
        assertFalse(result)
    }

    @Test
    fun startSimulationReturnsFalseWhenNoFileLoaded() {
        // Arrange
        val controller = SimulationController()

        // Act
        val result = controller.startSimulation()

        // Assert
        assertFalse(result)
    }

    @Test
    fun hasMoreUpdatesReturnsFalseWhenNotStarted() {
        // Arrange
        val controller = SimulationController()

        // Act
        val result = controller.hasMoreUpdates()

        // Assert
        assertFalse(result)
    }

    @Test
    fun processNextUpdateReturnsFalseWhenNotStarted() {
        // Arrange
        val controller = SimulationController()

        // Act
        val result = controller.processNextUpdate()

        // Assert
        assertFalse(result)
    }

    @Test
    fun stopSimulationCompletesWithoutError() {
        // Arrange
        val controller = SimulationController()

        // Act & Assert - Should not throw
        controller.stopSimulation()
    }

    @Test
    fun startSimulationReturnsTrueWhenFileIsLoadedAndNotRunning() {
        // Arrange
        setPrivateField(simulationController, "isRunning", false)
        whenever(mockFileProcessor.isEmpty()).thenReturn(false)
        whenever(mockFileProcessor.getTotalUpdates()).thenReturn(5)
        doNothing().whenever(mockSimulator).clearAllShipments()

        // Act
        val result = simulationController.startSimulation()

        // Assert
        assertTrue(result)
        assertTrue(simulationController.isSimulationRunning())
        verify(mockSimulator).clearAllShipments()
    }



    @Test
    fun stopSimulationSetsRunningToFalseAndClosesFile() {
        // Arrange
        setPrivateField(simulationController, "isRunning", true)
        
        // Act
        simulationController.stopSimulation()
        
        // Assert
        assertFalse(simulationController.isSimulationRunning())
        verify(mockFileProcessor).close()
    }
    
    @Test
    fun processNextUpdateReturnsFalseWhenNotRunning() {
        // Arrange
        setPrivateField(simulationController, "isRunning", false)
        
        // Act
        val result = simulationController.processNextUpdate()
        
        // Assert
        assertFalse(result)
        verify(mockFileProcessor, never()).getNextUpdate()
    }
    
    @Test
    fun processNextUpdateReturnsFalseAndStopsSimulationWhenNoMoreUpdates() {
        // Arrange
        setPrivateField(simulationController, "isRunning", true)
        whenever(mockFileProcessor.getNextUpdate()).thenReturn(null)
        
        // Act
        val result = simulationController.processNextUpdate()
        
        // Assert
        assertFalse(result)
        assertFalse(simulationController.isSimulationRunning())
        verify(mockFileProcessor).close()
    }
    
    @Test
    fun processNextUpdateReturnsTrueWhenUpdateProcessedSuccessfully() {
        // Arrange
        setPrivateField(simulationController, "isRunning", true)
        whenever(mockFileProcessor.getNextUpdate()).thenReturn("UPDATE,123,456")
        
        // Act
        val result = simulationController.processNextUpdate()
        
        // Assert
        assertTrue(result)
        verify(mockUpdateProcessor).processUpdate("UPDATE,123,456")
    }
    
    @Test
    fun processNextUpdateHandlesExceptionsAndContinuesIfMoreUpdates() {
        // Arrange
        setPrivateField(simulationController, "isRunning", true)
        whenever(mockFileProcessor.getNextUpdate()).thenThrow(RuntimeException("Test exception"))
        whenever(mockFileProcessor.hasMoreUpdates()).thenReturn(true)
        
        // Act
        val result = simulationController.processNextUpdate()
        
        // Assert
        assertTrue(result)
    }
    
    @Test
    fun loadFileReturnsFalseWhenFileDoesntExist() {
        // Arrange
        val nonExistentFileName = "doesNotExist.txt"
        
        // Act
        val result = simulationController.loadFile(nonExistentFileName)
        
        // Assert
        assertFalse(result)
    }
    
    @Test
    fun resetSimulationStopsSimulationAndClearsData() {
        // Arrange
        setPrivateField(simulationController, "isRunning", true)
        
        // Act
        simulationController.resetSimulation()
        
        // Assert
        assertFalse(simulationController.isSimulationRunning())
        verify(mockFileProcessor).close()
        verify(mockFileProcessor).reset()
    }
    
    // Utility method to set private fields via reflection
    private fun setPrivateField(instance: Any, fieldName: String, value: Any) {
        val field = instance.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(instance, value)
    }
}