package com.example.emobilitychargingstations.domain.stations

import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.UserLocation

class StationsUseCase(val stationsRepository: StationsRepository) {

    suspend fun insertStations(stations: Stations) {
        stationsRepository.insertStations(
            stations
        )
    }

    suspend fun getStationsLocal(): Stations? {
        return stationsRepository.getStationsLocal()
    }

    suspend fun getStationsRemote(userLocation: UserLocation): Stations? {
        return stationsRepository.getStationsRemote(userLocation)
    }

}