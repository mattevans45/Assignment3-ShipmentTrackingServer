package org.example.project.client

import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.model.Shipment
import org.example.project.model.ShipmentStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TrackingClientApp() {
    val scope = rememberCoroutineScope()
    val viewHelper = remember { TrackerViewHelper(scope) }
    var shipmentId by remember { mutableStateOf("") }
    val toastMessages = remember { mutableStateListOf<ToastMessage>() }

    fun showToast(message: String, isError: Boolean) {
        val newToast = ToastMessage(message, isError)
        toastMessages.add(newToast)
        scope.launch {
            delay(3000)
            toastMessages.remove(newToast)
        }
    }

    fun trackShipment() {
        if (shipmentId.isBlank()) {
            showToast("Please enter a shipment ID", true)
            return
        }
        viewHelper.trackShipment(shipmentId)
        shipmentId = ""
    }

    fun stopTrackingShipment(shipmentIdToStop: String) {
        viewHelper.stopTracking(shipmentIdToStop)
    }

    // Effect to handle toasts from the viewHelper
    LaunchedEffect(viewHelper.statusMessage.collectAsState().value) {
        viewHelper.statusMessage.value?.let {
            showToast(it.message, it.isError)
            viewHelper.clearStatusMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📦 Shipment Tracking Client",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Track", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ShipmentDisplay(
    viewHelper: TrackerViewHelper,
    onStopTracking: (String) -> Unit
) {
    val trackedShipments = viewHelper.trackedShipmentData

    if (trackedShipments.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(
                items = trackedShipments.values.toList(),
                key = { shipment -> "${shipment.id}_${shipment.updateHistory.size}_${shipment.status}" }
            ) { shipment ->
                ShipmentCard(
                    shipment = shipment,
                    onStopTracking = { onStopTracking(shipment.id) }
                )
            }
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📋 No shipments being tracked\nEnter a shipment ID above to start tracking",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ShipmentCard(shipment: Shipment, onStopTracking: () -> Unit) {
    fun formatTimestamp(timestamp: Long?): String {
        return timestamp?.let {
            val instant = Instant.ofEpochMilli(it)
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
            instant.atZone(ZoneId.systemDefault()).format(formatter)
        } ?: "Unknown"
    }

    val deliveryText = formatTimestamp(shipment.expectedDeliveryDate)
    val statusUpdates = shipment.updateHistory.filter { it.previousStatus != it.newStatus }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    text = "📦 ${shipment.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onStopTracking, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, "Stop tracking", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (shipment.status) {
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
                        text = shipment.status.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("📍 Location:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(shipment.currentLocation ?: "Unknown", fontWeight = FontWeight.Medium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("🚚 Expected Delivery:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(deliveryText, fontWeight = FontWeight.Medium)
            }

            if (statusUpdates.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📊 Status History:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    statusUpdates.forEach { update ->
                        Text("• ${update.previousStatus} → ${update.newStatus} on ${formatTimestamp(update.timestamp)}", fontSize = 14.sp)
                    }
                }
            }

            if (shipment.notes.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📝 Notes:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    shipment.notes.forEach { note ->
                        val isViolation = note.startsWith("VIOLATION:")
                        Text(
                            text = "• $note",
                            color = if (isViolation) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isViolation) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToastOverlay(toastMessages: List<ToastMessage>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .widthIn(max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            toastMessages.forEach { toast ->
                key(toast.id) {
                    ToastMessageDisplay(message = toast.message, isError = toast.isError)
                }
            }
        }
    }
}

@Composable
fun ToastMessageDisplay(message: String, isError: Boolean) {
    val backgroundColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val textColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer

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
            modifier = Modifier.shadow(4.dp, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
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
