package org.example.project.server

import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun startServer() {
    embeddedServer(Netty, port = 8080, module = Application::shipmentModule).start(wait = false)
}

fun main() {
    startServer()
}