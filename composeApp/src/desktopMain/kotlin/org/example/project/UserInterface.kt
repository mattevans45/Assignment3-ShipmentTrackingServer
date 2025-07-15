package org.example.project

class UserInterface(
    private val viewHelper: TrackerViewHelper,
    private val simulationController: SimulationController
) {
    private val currentTrackingIds: MutableSet<String> = mutableSetOf()
    private val simulator: TrackingSimulator = TrackingSimulator.getInstance()


    fun handleTrackingRequest(id: String) {
        currentTrackingIds.add(id)
        viewHelper.trackShipment(id)
        println("DEBUG: UserInterface tracking request for $id")
    }

    fun displayTrackingForm() {
        println("=== Shipment Tracking Form ===")
        println("Enter shipment ID to track:")
    }

    suspend fun handleStopTrackingRequest(id: String) {
        if (!isTrackingShipment(id)) {
            println("Not currently tracking shipment: $id")
            return
        }

        currentTrackingIds.remove(id)
        viewHelper.stopTracking(id)
        println("Stopped tracking shipment: $id")
    }

    fun displayShipmentInfo(shipment: Shipment) {
        println("=== Shipment Information ===")
        println("ID: ${shipment.getId()}")
        println("Status: ${shipment.getStatus()}")
        println("Location: ${shipment.getCurrentLocation()}")
        println("Expected Delivery: ${shipment.getFormattedDeliveryDate() ?: "Not specified"}")
        println("Notes: ${shipment.getNotes()?.toList()?.joinToString("; ") ?: "No notes"}")
        println("Update History:")
        shipment.getFormattedUpdateHistory().forEach { println("  - $it") }
    }
    fun displayErrorMessage(message: String) {
        println("ERROR: $message")
    }

    fun displayShipmentNotFound(shipmentId: String) {
        println("Shipment not found: $shipmentId")
    }

    fun startSimulation() {
        simulationController.startSimulation()
        println("Simulation started")
    }

    fun stopSimulation() {
        simulationController.stopSimulation()
        println("Simulation stopped")
    }

    fun isTrackingShipment(id: String): Boolean = currentTrackingIds.contains(id)
}