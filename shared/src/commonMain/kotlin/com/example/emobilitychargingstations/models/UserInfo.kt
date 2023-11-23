package com.example.emobilitychargingstations.models

data class UserInfo(
    val chargerType: ChargerTypesEnum?,
    val favoriteStations: MutableList<Station>?
)
