package org.example.project

class SimulationController(
    private val fileProcessor: FileProcessor = FileProcessor(),
    private val updateProcessor: UpdateProcessor = UpdateProcessor(),


) {
    private val simulator: TrackingSimulator get() = TrackingSimulator.getInstance()
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
            simulator.clearAllShipments()

            isRunning = true
            println("DEBUG: Simulation started with ${fileProcessor.getTotalUpdates()} updates")

            return true
        } catch (e: Exception) {
            fileProcessor.close()
            println("ERROR: Failed to start simulation: ${e.message}")
            isRunning = false
            throw e
        }
    }

    fun stopSimulation() {
        println("DEBUG: Stopping simulation...")
        isRunning = false
        cleanup()
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
            return fileProcessor.hasMoreUpdates()
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

    fun cleanup() {
        fileProcessor.close()
    }

    fun resetSimulation() {
        stopSimulation()
        simulator.clearAllShipments()
        fileProcessor.reset()
        println("DEBUG: Simulation controller reset")
    }
}