package com.example.emobilitychargingstations.data.stations

import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.UserInfo
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.flow.Flow

interface StationsRepository {
    suspend fun insertStations(stations: Stations)
    suspend fun getStationsLocal(): Stations?

    suspend fun getStationsRemote(userLocation: UserLocation): Stations?

    fun getUserInfo(): UserInfo?

    suspend fun getUserInfoAsFlow(): Flow<UserInfo?>

    suspend fun setUserInfo(userInfo: UserInfo)
}