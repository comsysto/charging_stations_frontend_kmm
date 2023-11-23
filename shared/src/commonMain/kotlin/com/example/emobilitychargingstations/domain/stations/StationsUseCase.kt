package com.example.emobilitychargingstations.domain.stations

import com.example.emobilitychargingstations.SHOULD_TRY_API_REQUEST
import com.example.emobilitychargingstations.STATION_REQUEST_REPEAT_TIME_MS
import com.example.emobilitychargingstations.data.extensions.getStationsClosestToUserLocation
import com.example.emobilitychargingstations.SharedFunctions
import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.data.stations.toStationList
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class StationsUseCase(private val stationsRepository: StationsRepository) {

    private var userLocation: UserLocation? = null

    suspend fun insertStations(stations: Stations) {
        stationsRepository.insertStations(
            stations
        )
    }

    suspend fun getStationsLocal(): Stations? {
        var localStations = stationsRepository.getStationsLocal()
        if (localStations == null) {
            localStations = SharedFunctions().getStationsFromJson()
            localStations?.let {
                insertStations(it)
            }
        }
        return localStations
    }

    suspend fun getStationsRemote(userLocation: UserLocation?): List<Station>? {
        return stationsRepository.getStationsRemote(userLocation)?.toStationList()
    }

    fun setTemporaryLocation(newLocation: UserLocation?) {
        userLocation = newLocation
    }

    fun startRepeatingRequest(initialLocation: UserLocation?) = flow {
        val localStations = getStationsLocal()
        userLocation = initialLocation
        if (userLocation != null) emit(localStations?.getStationsClosestToUserLocation(userLocation!!.latitude, userLocation!!.longitude))
        else emit(localStations!!.features)
        while (true) {
            var remoteStations: List<Station>? = null
            if (SharedFunctions().isDebug && SHOULD_TRY_API_REQUEST)  {
                try {
                    remoteStations = stationsRepository.getStationsRemote(userLocation)?.toStationList()
                } catch (e: Exception) {
                }
            }
            val stationList = mutableListOf<Station>()
            localStations?.features?.let {
                it.forEach { station ->
                    station.properties.availableChargingStations = (0..(station.properties.capacity?.toInt() ?: 1)).random()
                }
                stationList.addAll(it)
            }
            remoteStations?.let {
                stationList.addAll(it)
            }
            var resultingList = stationList.toList()
            userLocation?.let {
                resultingList = resultingList.getStationsClosestToUserLocation(it.latitude, it.longitude)
            }
            emit(resultingList)
            delay(STATION_REQUEST_REPEAT_TIME_MS)
        }
    }

}