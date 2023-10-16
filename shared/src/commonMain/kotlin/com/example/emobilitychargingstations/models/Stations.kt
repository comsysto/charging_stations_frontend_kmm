package com.example.emobilitychargingstations.models

import com.example.emobilitychargingstations.models.Station

@kotlinx.serialization.Serializable
data class Stations(
    val type: String,
    val features: List<Station>?
)

