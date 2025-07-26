package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.client.SimpleTrackingApp
import org.example.project.client.TrackingClientApp
import org.example.project.server.startServer


fun main() = application {
    startServer()
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