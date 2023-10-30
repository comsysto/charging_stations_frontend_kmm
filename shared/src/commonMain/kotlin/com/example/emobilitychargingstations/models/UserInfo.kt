package com.example.emobilitychargingstations.models

data class UserInfo(
    val chargerType: String?,
    val favoriteStations: MutableList<Station>?
)
