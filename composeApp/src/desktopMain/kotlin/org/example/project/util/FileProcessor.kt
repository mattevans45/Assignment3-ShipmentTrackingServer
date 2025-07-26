package org.example.project.util

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.LinkedList
import java.util.Queue

class FileProcessor : AutoCloseable {
    private var fileName: String = ""
    private val updateQueue: Queue<String> = LinkedList()
    private var fileReader: BufferedReader? = null
    private var currentLine: Int = 0
    private var totalUpdates: Int = 0
    private var processedUpdates: Int = 0

    override fun close() {
        try {
            fileReader?.close()
            fileReader = null
            println("DEBUG: FileProcessor - File closed successfully")
        } catch (e: IOException) {
            println("ERROR: FileProcessor - Error closing file: ${e.message}")
        }
    }

    fun loadFile(fileName: String): Boolean {
        close()

        return try {
            this.fileName = fileName
            fileReader = BufferedReader(FileReader(fileName))
            loadAllUpdates()
            println("DEBUG: FileProcessor loaded ${totalUpdates} updates from $fileName")
            true
        } catch (e: IOException) {
            println("ERROR: Failed to load file: $fileName - ${e.message}")
            close()
            false
        }
    }

    private fun loadAllUpdates() {
        updateQueue.clear()
        currentLine = 0
        processedUpdates = 0
        totalUpdates = 0

        try {
            fileReader?.use { reader ->
                reader.lineSequence().forEach { line ->
                    if (validateUpdateLine(line)) {
                        updateQueue.offer(line.trim())
                        totalUpdates++
                    }
                }
            }

            fileReader = null
        } catch (e: IOException) {
            println("ERROR: Error reading file: ${e.message}")
            close()
        }
    }

    fun getNextUpdate(): String? {
        return if (updateQueue.isNotEmpty()) {
            currentLine++
            processedUpdates++
            updateQueue.poll()
        } else {
            null
        }
    }

    fun hasMoreUpdates(): Boolean = updateQueue.isNotEmpty()

    fun validateUpdateLine(line: String): Boolean {
        if (line.isBlank()) return false

        val parts = line.split(",")
        if (parts.size < 3) return false

        val updateType = parts[0].trim()
        val shipmentId = parts[1].trim()
        val timestamp = parts[2].trim()

        return updateType.isNotBlank() &&
               shipmentId.isNotBlank() &&
               timestamp.toLongOrNull() != null
    }

    fun getTotalUpdates(): Int = totalUpdates

    fun isEmpty(): Boolean = updateQueue.isEmpty()


    fun getFileName(): String = fileName

    fun reset() {
        close()
        updateQueue.clear()
        fileName = ""
        currentLine = 0
        totalUpdates = 0
        processedUpdates = 0
    }
}