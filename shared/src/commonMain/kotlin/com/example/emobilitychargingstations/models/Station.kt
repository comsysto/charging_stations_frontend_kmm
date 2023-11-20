package com.example.emobilitychargingstations.models

import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class Station(
    val id: Long,
    val type: String?,
    val properties: StationProperties,
    val geometry: StationGeoData,
    @Transient
    var isNavigatingTo: Boolean = false

)
