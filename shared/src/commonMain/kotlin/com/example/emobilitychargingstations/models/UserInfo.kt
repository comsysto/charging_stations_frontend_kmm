package com.example.emobilitychargingstations.models

data class UserInfo(
    val filterProperties: StationFilterProperties?,
    val favoriteStations: MutableList<Station>?
) {
    fun initializeEmptyUserInfo(): UserInfo = UserInfo(StationFilterProperties(), mutableListOf())
}
