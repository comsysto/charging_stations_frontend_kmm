package com.example.emobilitychargingstations.data.stations

import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.StationsResponseModel
import com.example.emobilitychargingstations.models.UserLocation

interface StationsRepository {
    suspend fun insertStations(stations: Stations)
    suspend fun getStationsLocal(): Stations?

    suspend fun getStationsRemote(userLocation: UserLocation?): List<StationsResponseModel>?
}