package com.example.emobilitychargingstations.domain.stations

import com.example.emobilitychargingstations.SHOULD_TRY_API_REQUEST
import com.example.emobilitychargingstations.STATION_REQUEST_REPEAT_TIME_MS
import com.example.emobilitychargingstations.data.extensions.getStationsClosestToUserLocation
import com.example.emobilitychargingstations.SharedFunctions
import com.example.emobilitychargingstations.data.extensions.filterByChargerType
import com.example.emobilitychargingstations.data.extensions.filterByChargingType
import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.data.stations.toStationList
import com.example.emobilitychargingstations.domain.user.UserUseCase
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.UserInfo
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.coroutineContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StationsUseCase(private val stationsRepository: StationsRepository, private val userUseCase: UserUseCase) {

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

    fun startRepeatingRequest(initialLocation: UserLocation?) = channelFlow<List<Station>?> {
        val localStations = getStationsLocal()
        var userInfo = userUseCase.getUserInfo()
        var localStationsWithUserFilters = localStations?.features
        localStationsWithUserFilters?.let {
            localStationsWithUserFilters = applyUserFiltersToStations(it, userInfo)
        }
        userLocation = initialLocation
        if (userLocation != null) send(localStationsWithUserFilters?.getStationsClosestToUserLocation(userLocation!!.latitude, userLocation!!.longitude))
        else send(localStationsWithUserFilters)
        var resultingList = listOf<Station>()
        CoroutineScope(currentCoroutineContext()).launch {
            userUseCase.getUserInfoAsFlow().onEach {
                if ((userInfo?.filterProperties?.chargingType != it?.filterProperties?.chargingType
                            || userInfo?.filterProperties?.chargerType != it?.filterProperties?.chargerType) && resultingList.isNotEmpty()) {
                    userInfo = it
                    resultingList = applyUserFiltersToStations(localStations!!.features!!, userInfo)
                    send(resultingList)
                }
            }.collect()
        }
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
            resultingList = stationList.toList()
            userLocation?.let {
                resultingList = resultingList.getStationsClosestToUserLocation(it.latitude, it.longitude)
            }
            resultingList = applyUserFiltersToStations(resultingList, userInfo)
            send(resultingList)
            delay(STATION_REQUEST_REPEAT_TIME_MS)
        }
    }

    private fun applyUserFiltersToStations(stationList: List<Station>, userInfo: UserInfo?): List<Station> {
        var resultingList = stationList
        userInfo?.filterProperties?.chargingType?.let {
            resultingList = resultingList.filterByChargingType(it)
        }
        userInfo?.filterProperties?.chargerType?.let {
            resultingList = resultingList.filterByChargerType(it)
        }
        return resultingList
    }

}