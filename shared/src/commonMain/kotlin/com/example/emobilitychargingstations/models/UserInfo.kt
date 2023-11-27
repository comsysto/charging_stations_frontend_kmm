package com.example.emobilitychargingstations.models

data class UserInfo(
    val filterProperties: StationFilterProperties?,
    val favoriteStations: MutableList<Station>?
)
