package com.example.emobilitychargingstations.models

import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class StationProperties (
    val capacity: Double?,
    val data_source: String?,
    val dc_support: Boolean?,
    val max_kw: Double?,
    val operator: String?,
    val socket_type_list: List<String>?,
    val station_id: Long,
    val street: String?,
    val total_kw: Double?,
    val town: String?,
    @Transient
    var availableChargingStations: Int = 0
    ) {
}