//package org.example.project
//
//import androidx.compose.ui.window.ApplicationScope
//import androidx.compose.ui.window.application
//import io.ktor.http.HttpStatusCode
//import io.ktor.client.plugins.websocket.*
//import io.ktor.client.request.*
//import io.ktor.server.testing.*
//import kotlin.test.Test
//import kotlin.test.assertEquals
//// import shipmentModule from its correct package, for example:
//import org.example.project.shipmentModule
//
//
//class ShipmentRoutesTest {
//
//    private val client = createClient {
//        install(WebSockets)
//    }
//    private fun ApplicationScope.shipmentModule() {
//        TODO("Not yet implemented")
//    }
//
//    private fun testApplication(function: () -> MatchGroup?): Any {
//        TODO("Not yet implemented")
//    }
//
//    @Test
//    fun testPostShipments() = testApplication {
//        application {
//            TODO("Add the Ktor module for the test")
//        }
//        client.post("/shipments").apply {
//            TODO("Please write your test here")
//        }
//    }
//
//    @Test
//    fun testDeleteShipmentsId() = testApplication {
//        application {
//            TODO("Add the Ktor module for the test")
//        }
//        client.delete("/shipments/{id}").apply {
//            TODO("Please write your test here")
//        }
//    }
//
//    @Test
//    fun testPutShipmentsId() = testApplication {
//        application {
//            TODO("Add the Ktor module for the test")
//        }
//        client.put("/shipments/{id}").apply {
//            TODO("Please write your test here")
//        }
//    }
//}