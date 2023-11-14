package com.example.emobilitychargingstations.domain.stations

import com.example.emobilitychargingstations.data.extensions.getStationsClosestToUserLocation
import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.data.stations.toStationList
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class StationsUseCase(val stationsRepository: StationsRepository) {

    suspend fun insertStations(stations: Stations) {
        stationsRepository.insertStations(
            stations
        )
    }

    suspend fun getStationsLocal(): Stations? {
        return stationsRepository.getStationsLocal()
    }

    suspend fun getStationsRemote(userLocation: UserLocation?): List<Station>? {
        return stationsRepository.getStationsRemote(userLocation)?.toStationList()
    }

    fun startRepeatingRequest(userLocation: UserLocation?) = flow {
        while (true) {
            val remoteStations = stationsRepository.getStationsRemote(userLocation)?.toStationList()
            val localStations = stationsRepository.getStationsLocal()
            val stationList = mutableListOf<Station>()
            var resultingList = listOf<Station>()
            localStations?.features?.let {
                stationList.addAll(it)
            }
            remoteStations?.let {
                stationList.addAll(it)
            }
            resultingList = stationList.toList()
            userLocation?.let {
                resultingList = resultingList.getStationsClosestToUserLocation(it.latitude, it.longitude)
            }
            emit(resultingList)
            delay(20000)
        }
    }

}