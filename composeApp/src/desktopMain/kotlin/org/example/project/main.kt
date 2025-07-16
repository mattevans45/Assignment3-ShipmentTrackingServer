package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.annotation.processing.Generated

@Generated("org.example.project.MainKt")
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Assn2-ShipmentTrackingSimulator",
    ) {
        App()
    }
}