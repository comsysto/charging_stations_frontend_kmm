package com.example.emobilitychargingstations.domain.stations

@kotlinx.serialization.Serializable
data class StationProperties (
    val data_source: String,
    val operator: String?,
    val station_id: Long,
    val street: String?,
    val town: String?
    ) {
}