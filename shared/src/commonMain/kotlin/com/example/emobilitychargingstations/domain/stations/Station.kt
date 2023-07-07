package com.example.emobilitychargingstations.domain.stations

@kotlinx.serialization.Serializable
data class Station(
    val id: Long,
    val type: String?,
    val properties: StationProperties,
    val geometry: StationGeoData
)
