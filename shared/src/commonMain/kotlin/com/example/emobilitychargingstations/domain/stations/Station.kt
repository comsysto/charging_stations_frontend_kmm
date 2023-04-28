package com.example.emobilitychargingstations.domain.stations

data class Station(
    val id: Long?,
    val address: String,
    val ltd: Double,
    val lng: Double,
    val operator: String?,
    val capacity: String
)
