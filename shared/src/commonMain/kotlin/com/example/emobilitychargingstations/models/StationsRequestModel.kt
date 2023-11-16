package com.example.emobilitychargingstations.models

import kotlinx.serialization.Serializable

@Serializable
data class StationsRequestModel (
    val latitude: Double,
    val longitude: Double,
    val radius: Long,
    val limit: Long
)
