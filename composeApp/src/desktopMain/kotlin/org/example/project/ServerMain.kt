package org.example.project

import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::shipmentModule).start(wait = true)
}