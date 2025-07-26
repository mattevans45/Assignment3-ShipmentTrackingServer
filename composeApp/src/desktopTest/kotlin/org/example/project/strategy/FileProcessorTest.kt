package org.example.project.strategy

import org.example.project.util.FileProcessor
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class FileProcessorTest {
    private lateinit var fileProcessor: FileProcessor

    @BeforeTest
    fun setUp() {
        fileProcessor = FileProcessor()
    }

    @AfterTest
    fun tearDown() {
        fileProcessor.close()
    }

    @Test
    fun loadFileReturnsFalseWhenFileDoesntExist() {
        // Act
        val result = fileProcessor.loadFile("nonexistent.txt")

        // Assert
        assertFalse(result)
    }

    @Test
    fun loadFileReturnsTrueWhenFileExists(@TempDir tempDir: Path) {
        // Arrange
        val file = File(tempDir.toFile(), "test.txt")
        file.writeText("CREATED,123,456")

        // Act
        val result = fileProcessor.loadFile(file.absolutePath)

        // Assert
        assertTrue(result)
        assertEquals(1, fileProcessor.getTotalUpdates())
    }

    @Test
    fun loadFileProperlyLoadsMultipleUpdates(@TempDir tempDir: Path) {
        // Arrange
        val file = File(tempDir.toFile(), "test.txt")
        file.writeText("""
            CREATED,123,456
            SHIPPED,123,789
            DELIVERED,123,999
        """.trimIndent())

        // Act
        fileProcessor.loadFile(file.absolutePath)

        // Assert
        assertEquals(3, fileProcessor.getTotalUpdates())
        assertTrue(fileProcessor.hasMoreUpdates())
    }

    @Test
    fun getNextUpdateReturnsNullWhenNoUpdates() {
        // Act
        val result = fileProcessor.getNextUpdate()

        // Assert
        assertNull(result)
    }

    @Test
    fun getNextUpdateReturnsUpdatesInOrder(@TempDir tempDir: Path) {
        // Arrange
        val file = File(tempDir.toFile(), "test.txt")
        file.writeText("""
            CREATED,123,456
            SHIPPED,123,789
        """.trimIndent())
        fileProcessor.loadFile(file.absolutePath)

        // Act & Assert
        assertEquals("CREATED,123,456", fileProcessor.getNextUpdate())
        assertEquals("SHIPPED,123,789", fileProcessor.getNextUpdate())
        assertNull(fileProcessor.getNextUpdate())
    }

    @Test
    fun validateUpdateLineReturnsFalseForInvalidUpdates() {
        // Act & Assert
        assertFalse(fileProcessor.validateUpdateLine(""))
        assertFalse(fileProcessor.validateUpdateLine("INVALID"))
        assertFalse(fileProcessor.validateUpdateLine("CREATED,"))
        assertFalse(fileProcessor.validateUpdateLine("CREATED,123,abc"))
    }

    @Test
    fun validateUpdateLineReturnsTrueForValidUpdates() {
        // Act & Assert
        assertTrue(fileProcessor.validateUpdateLine("CREATED,123,456"))
        assertTrue(fileProcessor.validateUpdateLine("SHIPPED,ABC-123,1656789012"))
    }

    @Test
    fun resetClearsAllData(@TempDir tempDir: Path) {
        // Arrange
        val file = File(tempDir.toFile(), "test.txt")
        file.writeText("CREATED,123,456")
        fileProcessor.loadFile(file.absolutePath)

        // Act
        fileProcessor.reset()

        // Assert
        assertEquals(0, fileProcessor.getTotalUpdates())
        assertFalse(fileProcessor.hasMoreUpdates())
        assertEquals("", fileProcessor.getFileName())
    }
}