package com.example.emobilitychargingstations.domain.stations

@kotlinx.serialization.Serializable
data class Stations(
    val type: String,
    val features: List<Station>?
)
