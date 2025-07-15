package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

// Add the missing formatTimestamp function
private fun formatTimestamp(timestamp: Long): String {
    val instant = java.time.Instant.ofEpochMilli(timestamp)
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
    return instant.atZone(java.time.ZoneId.systemDefault()).format(formatter)
}

@Composable
@Preview
fun App() {
    MaterialTheme { ShipmentTrackingApp() }
}

@Composable
fun ShipmentTrackingApp() {
    // Create instances only once and reuse
    val simulator = TrackingSimulator.getInstance()
    val viewHelper = remember { TrackerViewHelper() }
    val simulationController = remember { SimulationController() }
    val userInterface = remember { UserInterface(viewHelper, simulationController) }
    
    // Create a coroutine scope for this composable
    val scope = rememberCoroutineScope()

    var isSimulationRunning by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var shipmentId by remember { mutableStateOf("") }

    // Connect viewHelper to TrackingSimulator once
    LaunchedEffect(viewHelper) {
        simulator.addObserver(viewHelper)
        println("DEBUG: App registered TrackerViewHelper as observer")
    }

    // Cleanup on disposal
    DisposableEffect(Unit) {
        onDispose {
            scope.launch {
                simulator.removeObserver(viewHelper)
            }
            viewHelper.cleanup()
            println("DEBUG: Cleaned up observer")
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with improved styling
        Text(
            text = "📦 Shipment Tracking Simulator",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Simulation Controls with Reset on Stop
        CompactSimulationPanel(
            isSimulationRunning = isSimulationRunning,
            onStartSimulation = {
                scope.launch {
                    try {
                        val fileName = "test.txt"
                        
                        // Use FileProcessor directly through the exposed method
                        val fileProcessor = simulationController.getFileProcessor()
                        
                        if (fileProcessor.loadFile(fileName)) {
                            simulationController.startSimulation()
                            isSimulationRunning = true
                            successMessage = "Simulation started successfully"
                            errorMessage = ""
                        } else {
                            errorMessage = "Failed to load $fileName file"
                            successMessage = ""
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error starting simulation: ${e.message}"
                        successMessage = ""
                    }
                }
            },
            onStopSimulation = {
                scope.launch {
                    try {
                        // Stop the simulation
                        simulationController.stopSimulation()
                        
                        // Clear all shipments and reset tracking
                        simulator.clearAllShipments()
                        viewHelper.resetSimulation()
                        
                        isSimulationRunning = false
                        successMessage = "Simulation stopped and reset - all shipments cleared"
                        errorMessage = ""
                        
                        println("DEBUG: Simulation stopped and all data cleared")
                    } catch (e: Exception) {
                        errorMessage = "Error stopping simulation: ${e.message}"
                        successMessage = ""
                    }
                }
            }
        )

        // Messages with better styling
        if (errorMessage.isNotEmpty()) {
            MessageCard(message = errorMessage, isError = true, onDismiss = { errorMessage = "" })
        }

        if (successMessage.isNotEmpty()) {
            MessageCard(
                message = successMessage,
                isError = false,
                onDismiss = { successMessage = "" }
            )
        }

        // Enhanced Tracking Input
        EnhancedTrackingInput(
            shipmentId = shipmentId,
            onShipmentIdChange = { shipmentId = it },
            onTrackShipment = {
                if (shipmentId.isNotBlank()) {
                    scope.launch {
                        try {
                            println("DEBUG: Attempting to track shipment: $shipmentId")
                            
                            // Simple validation: Check if shipment exists
                            val existingShipment = simulator.getShipment(shipmentId)
                            
                            if (existingShipment != null) {
                                // Shipment exists, proceed with normal tracking
                                userInterface.handleTrackingRequest(shipmentId)
                                viewHelper.trackShipment(shipmentId)
                                successMessage = "Started tracking shipment: $shipmentId"
                                errorMessage = ""
                                shipmentId = ""
                            } else {
                                // Shipment doesn't exist, show error
                                errorMessage = "⚠️ Shipment ID '$shipmentId' not found. Please verify the ID and ensure the simulation is running."
                                successMessage = ""
                                println("DEBUG: Shipment $shipmentId not found in simulator")
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error tracking shipment: ${e.message}"
                            successMessage = ""
                            println("ERROR: Exception while tracking shipment: ${e.message}")
                        }
                    }
                } else {
                    errorMessage = "Please enter a shipment ID"
                    successMessage = ""
                }
            }
        )

        // Expanded Shipment Information Display
        EnhancedShipmentDisplay(
            viewHelper = viewHelper,
            onStopTracking = { shipmentIdToStop ->
                scope.launch {
                    try {
                        println("DEBUG: Stopping tracking for: $shipmentIdToStop")
                        userInterface.handleStopTrackingRequest(shipmentIdToStop)
                        viewHelper.stopTracking(shipmentIdToStop)
                        successMessage = "Stopped tracking shipment: $shipmentIdToStop"
                        errorMessage = ""
                    } catch (e: Exception) {
                        errorMessage = "Error stopping tracking: ${e.message}"
                        successMessage = ""
                    }
                }
            }
        )
    }
}

@Composable
fun CompactSimulationPanel(
        isSimulationRunning: Boolean,
        onStartSimulation: () -> Unit,
        onStopSimulation: () -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
            shape = RoundedCornerShape(12.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                        modifier =
                                Modifier.size(10.dp)
                                        .shadow(2.dp, RoundedCornerShape(50))
                                        .then(
                                                Modifier.background(
                                                        if (isSimulationRunning) Color.Green
                                                        else Color.Red,
                                                        RoundedCornerShape(50)
                                                )
                                        )
                )
                Text(
                        text =
                                if (isSimulationRunning) "Simulation Running"
                                else "Simulation Stopped",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Compact buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                        onClick = onStartSimulation,
                        enabled = !isSimulationRunning,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) { Text("Start", fontSize = 14.sp) }

                Button(
                        onClick = onStopSimulation,
                        enabled = isSimulationRunning,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) { Text("Stop", fontSize = 14.sp) }
            }
        }
    }
}

@Composable
fun MessageCard(message: String, isError: Boolean, onDismiss: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E8)
                    ),
            shape = RoundedCornerShape(8.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = message,
                    color = if (isError) Color(0xFFD32F2F) else Color(0xFF388E3C),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Text("×", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun EnhancedTrackingInput(
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
                        placeholder = { Text("Enter shipment ID (e.g., S001)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onTrackShipment() }),
                        shape = RoundedCornerShape(12.dp)
                )

                Button(
                        onClick = onTrackShipment,
                        modifier = Modifier.height(56.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                ),
                        shape = RoundedCornerShape(12.dp)
                ) { Text("Track", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun EnhancedShipmentDisplay(
    viewHelper: TrackerViewHelper,
    onStopTracking: (String) -> Unit
) {
    // Observe both the tracked shipments AND the update trigger
    val trackedShipments by viewHelper.trackedShipments
    val updateTrigger by viewHelper.updateTrigger
    
    // Add this debugging to see if recomposition is happening
    LaunchedEffect(updateTrigger) {
        println("DEBUG: EnhancedShipmentDisplay - Trigger changed to: $updateTrigger")
        println("DEBUG: EnhancedShipmentDisplay - Tracked shipments: ${trackedShipments.size}")
        trackedShipments.forEach { (id, shipment) ->
            println("DEBUG: - Shipment $id: Status=${shipment.getStatus()}, Location=${shipment.getCurrentLocation()}")
        }
    }

    if (trackedShipments.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Show the trigger value to verify it's updating
                Text(
                    text = "🚚 Tracked Shipments (${trackedShipments.size}) [Update: $updateTrigger]",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trackedShipments.values.forEach { shipment ->
                        // Use both shipment ID and update trigger as key
                        key("${shipment.getId()}_$updateTrigger") {
                            EnhancedShipmentCard(
                                shipment = shipment,
                                updateTrigger = updateTrigger, // Pass trigger to card
                                onStopTracking = { onStopTracking(shipment.getId()) }
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Show empty state with trigger value
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
                    text = "📋 No shipments being tracked [Trigger: $updateTrigger]\nEnter a shipment ID above to start tracking",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun EnhancedShipmentCard(shipment: Shipment, onStopTracking: () -> Unit, updateTrigger: Int) {
    // Check if this is a placeholder shipment
    val isPlaceholder = shipment.getNotes()?.contains("⚠️ Shipment not found in system") == true
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaceholder) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row with ID and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isPlaceholder) {
                        Text(
                            text = "⚠️",
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "📦 ${shipment.getId()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isPlaceholder) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }

                IconButton(onClick = onStopTracking, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop tracking",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Show warning banner for placeholder shipments
            if (isPlaceholder) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⚠️ Shipment Not Found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "This shipment ID was not found in the system. Please verify the ID is correct and ensure the simulation is running.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Status with improved styling
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
                        containerColor = if (isPlaceholder) {
                            Color(0xFF9E9E9E)
                        } else {
                            when (shipment.getStatus()) {
                                ShipmentStatus.DELIVERED -> Color(0xFF4CAF50)
                                ShipmentStatus.LOST, ShipmentStatus.CANCELED -> Color(0xFFF44336)
                                ShipmentStatus.DELAYED -> Color(0xFFFF9800)
                                ShipmentStatus.SHIPPED -> Color(0xFF2196F3)
                                else -> Color(0xFF9E9E9E)
                            }
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isPlaceholder) "NOT FOUND" else shipment.getStatus().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Location - ALWAYS show this row, with 'Unknown' if null
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

            // Expected delivery date - ALWAYS show this row, with 'Unknown' if not set
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🚚 Expected Delivery:",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                val deliveryDate = shipment.getExpectedDeliveryDate()
                val deliveryText = if (deliveryDate == null || deliveryDate <= 0) {
                    "Unknown"
                } else {
                    formatTimestamp(deliveryDate)
                }
                
                Text(
                    text = deliveryText,
                    fontWeight = FontWeight.Medium,
                    color = if (deliveryDate == null || deliveryDate <= 0) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // Notes if available
            shipment.getNotes()?.let { notes ->
                if (notes.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "📝 Notes:",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            // Split notes by newline and display each on its own line
                            val noteLines = notes.split("\n").filter { it.isNotBlank() }
                            Column(
                                modifier = Modifier.padding(start = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                noteLines.forEach { note ->
                                    Text(
                                        text = "• $note",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Status updates - refresh this section every time
            val statusUpdates = shipment.getUpdates().filter { update ->
                // Only show updates where the status actually changed
                !update.getPreviousStatus().equals(update.getNewStatus(), ignoreCase = true)
            }

            if (statusUpdates.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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

                        // Show all status updates with improved formatting
                        statusUpdates.forEach { update ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Main status update text
                                    Text(
                                        text = "Shipment went from ${update.getPreviousStatus()} to ${update.getNewStatus()} on ${formatTimestamp(update.getTimestamp())}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    // Show location if available, with 'Unknown' if null
                                    val updateLocation = update.getLocation()
                                    Text(
                                        text = "📍 Location: ${if (updateLocation.isNullOrEmpty()) "Unknown" else updateLocation}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )

                                    // Show additional update info if available (but not notes)
                                    update.getNotes()?.let { notes ->
                                        if (notes.isNotEmpty() && !notes.contains("Note added", ignoreCase = true)) {
                                            Text(
                                                text = "ℹ️ Details: $notes",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Show total count of status updates
                        Text(
                            text = "Total status updates: ${statusUpdates.size}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}
