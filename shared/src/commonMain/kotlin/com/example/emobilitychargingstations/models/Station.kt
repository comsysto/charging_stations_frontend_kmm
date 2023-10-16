package com.example.emobilitychargingstations.models

@kotlinx.serialization.Serializable
data class Station(
    val id: Long,
    val type: String?,
    val properties: StationProperties,
    val geometry: StationGeoData
)
