package org.example.project

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*

class FileProcessor {
    private var fileName: String = ""
    private val updateQueue: Queue<String> = LinkedList()
    private var fileReader: BufferedReader? = null
    private var currentLine: Int = 0
    private var totalUpdates: Int = 0
    private var processedUpdates: Int = 0

    fun loadFile(fileName: String): Boolean {
        return try {
            this.fileName = fileName
            fileReader = BufferedReader(FileReader(fileName))
            loadAllUpdates()
            println("DEBUG: FileProcessor loaded ${totalUpdates} updates from $fileName")
            true
        } catch (e: IOException) {
            println("ERROR: Failed to load file: $fileName - ${e.message}")
            false
        }
    }

    private fun loadAllUpdates() {
        updateQueue.clear()
        currentLine = 0
        processedUpdates = 0
        totalUpdates = 0
        
        fileReader?.use { reader ->
            reader.lineSequence().forEach { line ->
                if (validateUpdateLine(line)) {
                    updateQueue.offer(line.trim())
                    totalUpdates++
                }
            }
        }
    }

    fun getNextUpdate(): String? {
        return if (updateQueue.isNotEmpty()) {
            currentLine++
            processedUpdates++
            val nextUpdate = updateQueue.poll()
            println("DEBUG: FileProcessor returning update $processedUpdates/$totalUpdates: $nextUpdate")
            nextUpdate
        } else {
            println("DEBUG: FileProcessor has no more updates")
            null
        }
    }

    fun hasMoreUpdates(): Boolean = updateQueue.isNotEmpty()

    fun closeFile() {
        fileReader?.close()
        fileReader = null
    }

    fun validateUpdateLine(line: String): Boolean {
        if (line.isBlank()) return false
        
        val parts = line.split(",")
        if (parts.size < 3) return false
        
        // Basic validation: updateType, shipmentId, timestamp
        val updateType = parts[0].trim()
        val shipmentId = parts[1].trim()
        val timestamp = parts[2].trim()
        
        return updateType.isNotBlank() && 
               shipmentId.isNotBlank() && 
               timestamp.toLongOrNull() != null
    }

    fun resetToBeginning() {
        updateQueue.clear()
        currentLine = 0
        processedUpdates = 0
        if (fileName.isNotEmpty()) {
            loadFile(fileName)
        }
    }
    
    // Methods needed by SimulationController
    fun getTotalUpdates(): Int = totalUpdates
    fun getProcessedUpdatesCount(): Int = processedUpdates
    fun getCurrentLineNumber(): Int = currentLine
    
    fun getProgress(): Float = if (totalUpdates > 0) {
        processedUpdates.toFloat() / totalUpdates.toFloat()
    } else 0f
    
    fun getRemainingUpdates(): Int = totalUpdates - processedUpdates
    
    fun isEmpty(): Boolean = updateQueue.isEmpty()
    
    fun getFileName(): String = fileName
}