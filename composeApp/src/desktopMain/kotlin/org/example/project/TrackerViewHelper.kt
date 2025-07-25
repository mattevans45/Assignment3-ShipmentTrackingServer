package org.example.project

import androidx.compose.runtime.mutableStateMapOf
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

data class ToastMessage(
    val message: String,
    val isError: Boolean,
    val id: Long = System.currentTimeMillis()
)

class TrackerViewHelper(private val coroutineScope: CoroutineScope) : Observer {

    private val _trackedShipmentData = mutableStateMapOf<String, Shipment>()
    val trackedShipmentData: Map<String, Shipment> = _trackedShipmentData

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<ToastMessage?>(null)
    val statusMessage = _statusMessage.asStateFlow()

    private var session: ClientWebSocketSession? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        serializersModule = SerializersModule {
            polymorphic(Shipment::class) {
                subclass(StandardShipment::class); subclass(ExpressShipment::class); subclass(OvernightShipment::class); subclass(BulkShipment::class)
            }
        }
        classDiscriminator = "shipment_type"
    }

    private val client = HttpClient(CIO) {
        install(WebSockets)
        install(ContentNegotiation) { json(json) }
    }
    private val backendUrl = "localhost"
    private val backendPort = 8080

    init {
        // Start and maintain the WebSocket connection as long as the scope is active.
        coroutineScope.launch {
            connectAndListen()
        }
    }

    private suspend fun connectAndListen() {
        try {
            client.webSocket(method = HttpMethod.Get, host = backendUrl, port = backendPort, path = "/track") {
                session = this
                _statusMessage.value = ToastMessage("Connected to tracking server.", false)

                // Resubscribe to any shipments we are already tracking
                _trackedShipmentData.keys.forEach { id ->
                    session?.send(Frame.Text("track,$id"))
                }

                // Listen for incoming messages
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val shipmentJson = frame.readText()
                        val updatedShipment = json.decodeFromString<Shipment>(shipmentJson)
                        onShipmentUpdated(updatedShipment)
                    }
                }
            }
        } catch (e: Exception) {
            _statusMessage.value = ToastMessage("Connection lost. Reconnecting...", true)
            session = null
            delay(5000) // Wait 5 seconds before retrying
            if (coroutineScope.isActive) {
                connectAndListen()
            }
        }
    }

    fun trackShipment(id: String) {
        if (id.isBlank() || _trackedShipmentData.containsKey(id)) return

        coroutineScope.launch {
            try {
                // Get the initial state via HTTP
                val shipment = client.get("http://$backendUrl:$backendPort/shipments/$id").body<Shipment>()
                onShipmentCreated(shipment)
                _statusMessage.value = ToastMessage("Started tracking shipment: $id", false)

                // Subscribe to WebSocket updates for this shipment
                session?.send(Frame.Text("track,$id"))
            } catch (e: Exception) {
                _statusMessage.value = ToastMessage("Error tracking shipment $id: Not found", true)
            }
        }
    }

    fun stopTracking(id: String) {
        val shipment = _trackedShipmentData.remove(id)
        shipment?.removeObserver(this)
        _statusMessage.value = ToastMessage("Stopped tracking shipment: $id", false)

        coroutineScope.launch {
            session?.send(Frame.Text("untrack,$id"))
        }
    }

    fun sendUpdateCommand(command: String) {
        coroutineScope.launch {
            _isLoading.value = true
            try {
                val parts = command.split(",")
                if (parts.size < 3) {
                    _statusMessage.value = ToastMessage("Invalid command format", true)
                    return@launch
                }

                val payload = ShipmentUpdatePayload(
                    updateType = parts[0].trim(),
                    shipmentId = parts[1].trim(),
                    timestamp = parts[2].trim().toLongOrNull() ?: System.currentTimeMillis(),
                    otherInfo = if (parts.size > 3) parts.drop(3).joinToString(",").trim() else null
                )

                val response = client.post("http://$backendUrl:$backendPort/shipments") {
                    contentType(ContentType.Application.Json)
                    setBody(payload)
                }

                if (response.status.isSuccess()) {
                    _statusMessage.value = ToastMessage("Update sent successfully", false)
                    // No need to re-fetch; the WebSocket will push the update automatically.
                } else {
                    _statusMessage.value = ToastMessage("Server error: ${response.status.value}", true)
                }
            } catch (e: Exception) {
                _statusMessage.value = ToastMessage("Network error: ${e.message}", true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // --- Observer Pattern Implementation ---
    override fun onShipmentUpdated(shipment: Shipment) {
        if (_trackedShipmentData.containsKey(shipment.id)) {
            shipment.addObserver(this)
            _trackedShipmentData[shipment.id] = shipment
            println("Observer (from WebSocket): Shipment ${shipment.id} was updated.")
        }
    }
    override fun onShipmentCreated(shipment: Shipment) {
        if (!_trackedShipmentData.containsKey(shipment.id)) {
            shipment.addObserver(this)
            _trackedShipmentData[shipment.id] = shipment
        }
    }
    override fun onShipmentNotFound(shipmentId: String) {
        if (_trackedShipmentData.containsKey(shipmentId)) {
            stopTracking(shipmentId)
        }
    }
}
