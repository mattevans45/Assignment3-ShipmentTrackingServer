package org.example.project.client

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.example.project.model.BulkShipment
import org.example.project.model.ExpressShipment
import org.example.project.model.OvernightShipment
import org.example.project.model.Shipment
import org.example.project.model.ShipmentUpdatePayload
import org.example.project.model.StandardShipment

@Composable
@Preview
fun SimpleTrackingApp() {
    var updateText by remember { mutableStateOf("") }
    var shipmentInfo by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()


    val client = remember {
        val shipmentModule = SerializersModule {
            polymorphic(Shipment::class) {
                subclass(StandardShipment::class)
                subclass(ExpressShipment::class)
                subclass(OvernightShipment::class)
                subclass(BulkShipment::class)
            }
        }
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    serializersModule = shipmentModule
                    classDiscriminator = "shipment_type"
                })
            }
        }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Send Shipment Update to Server", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        TextField(
            value = updateText,
            onValueChange = { updateText = it },
            label = { Text("Update String (e.g. created,s1,1721865600000,express)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val parts = updateText.split(",")
            if (parts.size >= 3) {
                val timestamp = parts[2].toLongOrNull()
                if (timestamp == null || timestamp <= 0) {
                    shipmentInfo = "Error: Please enter a valid numeric timestamp."
                    return@Button
                }


                scope.launch(Dispatchers.IO) {
                    try {

                        client.post("http://localhost:8080/shipments") {
                            contentType(ContentType.Application.Json)
                            setBody(
                                ShipmentUpdatePayload(
                                    updateType = parts[0].trim(),
                                    shipmentId = parts[1].trim(),
                                    timestamp = timestamp,
                                    otherInfo = if (parts.size > 3) parts.drop(3).joinToString(",").trim() else null
                                )
                            )
                        }

                        val shipments: List<Shipment> = client.get("http://localhost:8080/shipments").body()

                        val formattedResponse = if (shipments.isEmpty()) {
                            "No shipments on the server."
                        } else {
                            shipments.joinToString("\n\n---\n\n") { shipment ->
                                """
                                ID: ${shipment.id}
                                Type: ${shipment::class.simpleName?.replace("Shipment", "")}
                                Status: ${shipment.status}
                                Location: ${shipment.currentLocation ?: "N/A"}
                                Notes: ${shipment.notes.joinToString(", ")}
                                History: ${shipment.updateHistory.size} updates
                                """.trimIndent()
                            }
                        }

                        withContext(Dispatchers.Main) {
                            shipmentInfo = formattedResponse
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            shipmentInfo = "Error: ${e.message}"
                        }
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Send Update & Refresh Shipments")
        }
        Spacer(Modifier.height(16.dp))
        Text("All Shipments:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(shipmentInfo, modifier = Modifier.fillMaxWidth())
    }
}
