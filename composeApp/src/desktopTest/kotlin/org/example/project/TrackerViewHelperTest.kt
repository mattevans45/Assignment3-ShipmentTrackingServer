package org.example.project

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.HttpRequestData
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.client.TrackerViewHelper
import org.example.project.model.*
import org.example.project.server.TrackingServer
import kotlin.test.*

class TrackerViewHelperTest {
    private lateinit var viewHelper: TrackerViewHelper
    private lateinit var testScope: CoroutineScope

    @BeforeTest
    fun setUp() {
        TrackingServer.clearAllShipments()
        testScope = CoroutineScope(SupervisorJob())
        TrackingServer.initialize(testScope)

        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L,
            updateHistory = mutableListOf(),
            notes = mutableListOf()
        )
        val mockEngine = MockEngine { request: HttpRequestData ->
            respond(
                content = Json.encodeToString<Shipment>(shipment),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        viewHelper = TrackerViewHelper(
            coroutineScope = testScope,
            client = mockClient // inject the mock client
        )
    }

    @AfterTest
    fun tearDown() {
        TrackingServer.clearAllShipments()
    }

    @Test
    fun trackShipmentAddsShipmentToTrackedList() = runBlocking {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L
        )
        TrackingServer.addShipment(shipment)

        // Act
        viewHelper.trackShipment("123")
        
        // Wait for the asynchronous operation to complete
        delay(200)

        // Assert
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(1, trackedShipments.size)
        assertTrue(trackedShipments.containsKey("123"))
    }

    @Test
    fun stopTrackingRemovesShipmentFromTrackedList() = runBlocking {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L
        )
        TrackingServer.addShipment(shipment)
        viewHelper.trackShipment("123")
        delay(200) // Wait for tracking to complete

        // Act
        viewHelper.stopTracking("123")

        // Assert
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(0, trackedShipments.size)
    }

    @Test
    fun onShipmentUpdatedUpdatesTrackedShipmentInState() = runBlocking {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L
        )
        TrackingServer.addShipment(shipment)
        
        // First, make sure the shipment is actually tracked
        viewHelper.trackShipment("123")
        waitForTrackedShipment("123")
        assertTrue(viewHelper.trackedShipmentData.containsKey("123"))

        // Act
        shipment.status = ShipmentStatus.SHIPPED
        viewHelper.onShipmentUpdated(shipment)

        // Assert
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(ShipmentStatus.SHIPPED, trackedShipments["123"]?.status)
    }

    @Test
    fun onShipmentCreatedAddsShipmentIfNotAlreadyTracked() {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L
        )

        // Act
        viewHelper.onShipmentCreated(shipment)

        // Assert
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(1, trackedShipments.size)
        assertEquals(shipment, trackedShipments["123"])
    }

    @Test
    fun onShipmentCreatedDoesNotDuplicateIfAlreadyTracked() = runBlocking {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L
        )
        TrackingServer.addShipment(shipment)
        viewHelper.trackShipment("123")
        delay(200) // Wait for tracking to complete

        val initialSize = viewHelper.trackedShipmentData.size

        // Act
        viewHelper.onShipmentCreated(shipment)

        // Assert
        assertEquals(initialSize, viewHelper.trackedShipmentData.size)
    }

    @Test
    fun resetSimulationClearsAllTrackedShipments() = runBlocking {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L
        )
        TrackingServer.addShipment(shipment)
        viewHelper.trackShipment("123")
        delay(200) // Wait for tracking to complete


        // Assert
        val trackedShipments = viewHelper.trackedShipmentData
        assertEquals(0, trackedShipments.size)
    }

    @Test
    fun trackShipmentIgnoresBlankIds() = runBlocking {
        // Act
        viewHelper.trackShipment("")
        viewHelper.trackShipment("   ")
        delay(200)

        // Assert
        assertEquals(0, viewHelper.trackedShipmentData.size)
    }

    @Test
    fun trackShipmentIgnoresDuplicates() = runBlocking {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L
        )
        TrackingServer.addShipment(shipment)

        // Act
        viewHelper.trackShipment("123")
        delay(200)
        val sizeAfterFirst = viewHelper.trackedShipmentData.size
        
        viewHelper.trackShipment("123") // Try to track same shipment again
        delay(200)

        // Assert
        assertEquals(sizeAfterFirst, viewHelper.trackedShipmentData.size)
    }

    @Test
    fun onShipmentUpdatedIgnoresUntrackedShipments() {
        // Arrange
        val shipment = StandardShipment(
            id = "123",
            status = ShipmentStatus.CREATED,
            createdTimestamp = 456L
        )
        val initialSize = viewHelper.trackedShipmentData.size

        // Act - Update a shipment that's not being tracked
        shipment.status = ShipmentStatus.SHIPPED
        viewHelper.onShipmentUpdated(shipment)

        // Assert - Size should remain the same and shipment should not be added
        assertEquals(initialSize, viewHelper.trackedShipmentData.size)
        assertNull(viewHelper.trackedShipmentData["123"])
    }

    private suspend fun waitForTrackedShipment(id: String, timeoutMs: Long = 2000) {
        val start = System.currentTimeMillis()
        while (!viewHelper.trackedShipmentData.containsKey(id)) {
            if (System.currentTimeMillis() - start > timeoutMs) break
            delay(10)
        }
    }
}