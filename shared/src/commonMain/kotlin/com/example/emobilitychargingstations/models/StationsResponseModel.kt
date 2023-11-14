package com.example.emobilitychargingstations.models

import kotlinx.serialization.Serializable

@Serializable
data class StationsResponseModel(
    val id: Long,
    val stationId: Long,
    val point: String?,
    val dataSource: String?,
    val operator: String?,
    val street: String?,
//    val houseNumber: String?,
    val town: String?,
    val latitude: Double,
    val longitude: Double,
    var totalChargingStations: Int,
    var availableChargingStations: Int,
    var liveDataAvailable: String,
//    var connections: List<String>,
//    var chargingCapacities: List<String>
)
