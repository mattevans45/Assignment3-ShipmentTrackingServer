package org.example.project

import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

class SimulationController {
    private val fileProcessor: FileProcessor = FileProcessor()
    private val updateProcessor: UpdateProcessor = UpdateProcessor()
    private var isRunning: Boolean = false
    private var simulationJob: Job? = null
    
    private val simulationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun startSimulation() {
        if (isRunning) {
            println("DEBUG: Simulation already running")
            return
        }

        if (fileProcessor.isEmpty()) {
            println("ERROR: No file loaded or file is empty")
            return
        }

        isRunning = true
        simulationJob = simulationScope.launch {
            println("DEBUG: Starting simulation with ${fileProcessor.getTotalUpdates()} updates...")
            
            try {
                processUpdatesEverySecond()
            } catch (e: CancellationException) {
                println("DEBUG: Simulation cancelled")
            } catch (e: Exception) {
                println("ERROR: Simulation error: ${e.message}")
                e.printStackTrace()
            } finally {
                isRunning = false
                println("DEBUG: Simulation stopped")
            }
        }
    }

    private suspend fun processUpdatesEverySecond() {
        while (isRunning) {
            val nextUpdate = fileProcessor.getNextUpdate()
            
            if (nextUpdate == null) {
                println("DEBUG: No more updates to process - simulation complete")
                break
            }
            
            println("DEBUG: Processing update (${fileProcessor.getProcessedUpdatesCount()}/${fileProcessor.getTotalUpdates()}): $nextUpdate")
            updateProcessor.processUpdate(nextUpdate)
            
            delay(1.seconds)
        }
    }

    fun stopSimulation() {
        println("DEBUG: Stopping simulation...")
        isRunning = false
        simulationJob?.cancel()
        simulationJob = null
    }

    fun isSimulationRunning(): Boolean = isRunning

    // Expose FileProcessor for external file operations
    fun getFileProcessor(): FileProcessor = fileProcessor

    fun cleanup() {
        stopSimulation()
        fileProcessor.closeFile()
        simulationScope.cancel()
    }
}