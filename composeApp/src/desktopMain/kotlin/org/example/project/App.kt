package org.example.project

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import javax.annotation.processing.Generated


@Generated
@Composable
@Preview
fun App() {
    MaterialTheme { ShipmentTrackingApp() }
}

@Generated
data class ToastMessage(
    val message: String,
    val isError: Boolean,
    val id: Long = System.currentTimeMillis()
)
@Generated
@Composable
fun ShipmentTrackingApp() {
    val simulator = remember { TrackingSimulator.getInstance() }
    val viewHelper = remember { TrackerViewHelper() }
    val simulationController = remember { SimulationController() }
    val scope = rememberCoroutineScope()
    
    // Simple state variables
    var isSimulationRunning by remember { mutableStateOf(false) }
    var simulationJob by remember { mutableStateOf<Job?>(null) }
    var shipmentId by remember { mutableStateOf("") }
    
    // Toast system
    val toastMessages = remember { mutableStateListOf<ToastMessage>() }
    
    // Function to show a toast
    fun showToast(message: String, isError: Boolean) {
        val newToast = ToastMessage(message, isError)
        toastMessages.add(newToast)
        
        // Auto-dismiss after 3 seconds
        scope.launch {
            delay(3000)
            toastMessages.remove(newToast)
        }
    }

    LaunchedEffect(Unit) {
        simulator.addObserver(viewHelper)
        println("DEBUG: App - Observer registered")
    }

    DisposableEffect(Unit) {
        onDispose {
            simulator.removeObserver(viewHelper)
            println("DEBUG: App - Observer removed")
        }
    }

    fun startSimulation() {
        if (isSimulationRunning) return
        
        try {
            val fileName = "test.txt"
            
            if (simulationController.loadFile(fileName) && simulationController.startSimulation()) {
                isSimulationRunning = true
                showToast("Simulation started successfully", false)

                simulationJob = scope.launch {
                    while (isSimulationRunning && simulationController.hasMoreUpdates()) {
                        val hasMore = simulationController.processNextUpdate()
                        
                        if (!hasMore) {
                            isSimulationRunning = false
                            showToast("Simulation completed - all updates processed", false)
                            break
                        }
                        
                        delay(1000)
                    }
                }
            } else {
                showToast("Failed to start simulation - check if test.txt exists", true)
            }
        } catch (e: Exception) {
            showToast("Error starting simulation: ${e.message}", true)
        }
    }

    fun stopSimulation() {
        try {
            simulationJob?.cancel()
            simulationJob = null
            simulationController.stopSimulation()
            
            // Clear all data
            simulator.clearAllShipments()
            viewHelper.resetSimulation()
            
            isSimulationRunning = false
            showToast("Simulation stopped and reset", false)
        } catch (e: Exception) {
            showToast("Error stopping simulation: ${e.message}", true)
        }
    }

    fun trackShipment() {
        if (shipmentId.isBlank()) {
            showToast("Please enter a shipment ID", true)
            return
        }
        try {
            val existingShipment = simulator.getShipment(shipmentId)
            
            if (existingShipment != null) {
                viewHelper.trackShipment(shipmentId)
                showToast("Started tracking shipment: $shipmentId", false)
                shipmentId = ""
            } else {
                if (isSimulationRunning) {
                    showToast("Shipment ID '$shipmentId' not found", true)
                } else {
                    showToast("Shipment ID '$shipmentId' not found. Please start simulation first.", true)
                }
            }
        } catch (e: Exception) {
            showToast("Error tracking shipment: ${e.message}", true)
        }
    }

    fun stopTrackingShipment(shipmentIdToStop: String) {
        try {
            viewHelper.stopTracking(shipmentIdToStop)
            showToast("Stopped tracking shipment: $shipmentIdToStop", false)
        } catch (e: Exception) {
            showToast("Error stopping tracking: ${e.message}", true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📦 Shipment Tracking Simulator",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            SimulationPanel(
                isSimulationRunning = isSimulationRunning,
                onStartSimulation = ::startSimulation,
                onStopSimulation = ::stopSimulation
            )

            TrackingInput(
                shipmentId = shipmentId,
                onShipmentIdChange = { shipmentId = it },
                onTrackShipment = ::trackShipment
            )

            ShipmentDisplay(
                viewHelper = viewHelper,
                onStopTracking = ::stopTrackingShipment
            )
        }

        ToastOverlay(toastMessages = toastMessages)
    }
}
@Generated
@Composable
fun ToastOverlay(toastMessages: List<ToastMessage>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 35.dp, end = 35.dp, top = 250.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            toastMessages.forEach { toast ->
                ToastMessageDisplay(message = toast.message, isError = toast.isError)
            }
        }
    }
}

@Generated
@Composable
fun ToastMessageDisplay(message: String, isError: Boolean) {
    val backgroundColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E8)
    val textColor = if (isError) Color(0xFFD32F2F) else Color(0xFF388E3C)

    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 })
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .widthIn(max = 400.dp), // Wider toast for better readability
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message,
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SimulationPanel(
    isSimulationRunning: Boolean,
    onStartSimulation: () -> Unit,
    onStopSimulation: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier.size(8.dp)
                        .background(
                            if (isSimulationRunning) Color.Green else Color.Red,
                            RoundedCornerShape(50)
                        )
                )
                Text(
                    text = if (isSimulationRunning) "Simulation Running" else "Simulation Stopped",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onStartSimulation,
                    enabled = !isSimulationRunning,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Start", fontSize = 12.sp)
                }

                Button(
                    onClick = onStopSimulation,
                    enabled = isSimulationRunning,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Stop", fontSize = 12.sp)
                }
            }
        }
    }
}

@Generated
@Composable
fun TrackingInput(
    shipmentId: String,
    onShipmentIdChange: (String) -> Unit,
    onTrackShipment: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🔍 Track New Shipment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = shipmentId,
                    onValueChange = onShipmentIdChange,
                    label = { Text("Shipment ID") },
                    placeholder = { Text("Enter shipment ID (e.g., s10008)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onTrackShipment() }),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = onTrackShipment,
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Track", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Generated
@Composable
fun ShipmentDisplay(
    viewHelper: TrackerViewHelper,
    onStopTracking: (String) -> Unit
) {
    val trackedShipments by viewHelper.trackedShipmentData
    
    if (trackedShipments.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(
                items = trackedShipments.values.toList(),
                key = { shipment -> 
                    "${shipment.getId()}_${shipment.getUpdates().size}_${shipment.getStatus()}"
                }
            ) { shipment ->
                ShipmentCard(
                    shipment = shipment,
                    onStopTracking = { onStopTracking(shipment.getId()) }
                )
            }
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📋 No shipments being tracked\nEnter a shipment ID above to start tracking",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Generated
@Composable
fun ShipmentCard(shipment: Shipment, onStopTracking: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📦 ${shipment.getId()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(onClick = onStopTracking, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop tracking",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status:",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (shipment.getStatus()) {
                            ShipmentStatus.DELIVERED -> Color(0xFF4CAF50)
                            ShipmentStatus.LOST, ShipmentStatus.CANCELED -> Color(0xFFF44336)
                            ShipmentStatus.DELAYED -> Color(0xFFFF9800)
                            ShipmentStatus.SHIPPED -> Color(0xFF2196F3)
                            else -> Color(0xFF9E9E9E)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = shipment.getStatus().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📍 Location:",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                val location = shipment.getCurrentLocation()
                Text(
                    text = if (location.isNullOrEmpty()) "Unknown" else location,
                    fontWeight = FontWeight.Medium,
                    color = if (location.isNullOrEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🚚 Expected Delivery:",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                val deliveryText = shipment.getFormattedDeliveryDate()
                
                Text(
                    text = deliveryText,
                    fontWeight = FontWeight.Medium,
                    color = if (deliveryText == "Unknown") {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            val statusUpdates = shipment.getUpdates().filter { update ->
                !update.getPreviousStatus().equals(update.getNewStatus(), ignoreCase = true)
            }
            val notes = shipment.getNotes()
            
            if (statusUpdates.isNotEmpty() || notes.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (statusUpdates.isNotEmpty()) {
                        Card(
                            modifier = Modifier.weight(if (notes.isEmpty()) 1f else 0.6f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "📊 Status History:",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                statusUpdates.forEach { update ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "Shipment went from ${update.getPreviousStatus()} to ${update.getNewStatus()} on ${update.getFormattedTimestamp()}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            update.getNotes()?.let { updateNotes ->
                                                if (updateNotes.isNotEmpty()) {
                                                    Text(
                                                        text = "ℹ️ $updateNotes",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = "Total: ${statusUpdates.size}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                    if (notes.isNotEmpty()) {
                        Card(
                            modifier = Modifier.weight(if (statusUpdates.isEmpty()) 1f else 0.4f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "📝 Notes:",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                notes.filter { it.isNotBlank() }.forEach { note ->
                                    Text(
                                        text = "• $note",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

