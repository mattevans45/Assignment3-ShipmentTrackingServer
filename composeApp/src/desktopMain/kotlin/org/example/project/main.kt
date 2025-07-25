package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.client.TrackingClientApp
import javax.annotation.processing.Generated

@Generated("org.example.project.MainKt")
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Assn3-ShipmentTrackingSimulator",
    ) {
        TrackingClientApp()
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Simple Tracking App",
    ) {
        SimpleTrackingApp()
    }
}