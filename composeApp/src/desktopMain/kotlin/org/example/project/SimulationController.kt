package org.example.project

class SimulationController {
    private val fileProcessor: FileProcessor = FileProcessor()
    private val updateProcessor: UpdateProcessor = UpdateProcessor()
    private var isRunning: Boolean = false

    fun startSimulation(): Boolean {
        if (isRunning) {
            println("DEBUG: Simulation already running")
            return false
        }

        if (fileProcessor.isEmpty()) {
            println("ERROR: No file loaded or file is empty")
            return false
        }

        try {
            // Reset the simulation state
            TrackingSimulator.getInstance().clearAllShipments()
            
            // Initialize the simulation
            isRunning = true
            println("DEBUG: Simulation started with ${fileProcessor.getTotalUpdates()} updates")
            
            return true
        } catch (e: Exception) {
            fileProcessor.close() // Ensure file is closed if exception occurs
            println("ERROR: Failed to start simulation: ${e.message}")
            isRunning = false
            throw e
        }
    }

    fun stopSimulation() {
        println("DEBUG: Stopping simulation...")
        isRunning = false
        fileProcessor.close() // Ensure file is closed when simulation stops
    }

    fun isSimulationRunning(): Boolean = isRunning

    fun processNextUpdate(): Boolean {
        if (!isSimulationRunning()) return false
        
        try {
            val nextUpdate = fileProcessor.getNextUpdate()
            
            if (nextUpdate == null) {
                println("DEBUG: No more updates to process - simulation complete")
                stopSimulation()
                return false
            }
            
            updateProcessor.processUpdate(nextUpdate)
            return true
        } catch (e: Exception) {
            println("ERROR: Failed to process update: ${e.message}")
            // Continue running but log the error
            return fileProcessor.hasMoreUpdates() // Return true if we have more updates to try
        }
    }

    fun loadFile(fileName: String): Boolean {
        return try {
            val file = java.io.File(fileName)
            if (!file.exists()) {
                println("ERROR: File not found: ${file.absolutePath}")
                return false
            }
            
            println("DEBUG: Loading file: ${file.absolutePath}")
            val success = fileProcessor.loadFile(fileName)
            
            if (success) {
                println("DEBUG: Successfully loaded file with ${fileProcessor.getTotalUpdates()} updates")
            } else {
                println("ERROR: Failed to load file")
            }
            
            success
        } catch (e: Exception) {
            println("ERROR: Error loading file: ${e.message}")
            false
        }
    }
    fun hasMoreUpdates(): Boolean = fileProcessor.hasMoreUpdates()

    // Add a cleanup method for the controller
    fun cleanup() {
        fileProcessor.close()
    }

    fun resetSimulation() {
        stopSimulation()
        TrackingSimulator.getInstance().clearAllShipments()
        fileProcessor.reset()
        println("DEBUG: Simulation controller reset")
    }
}