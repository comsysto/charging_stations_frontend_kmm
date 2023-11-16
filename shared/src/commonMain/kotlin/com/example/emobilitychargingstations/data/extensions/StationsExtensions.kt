package com.example.emobilitychargingstations.data.extensions

import com.example.emobilitychargingstations.models.ChargerTypesEnum
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import kotlin.math.abs

fun Stations.getStationsClosestToUserLocation(userLat: Double, userLng: Double): List<Station> {
    val filteredList = mutableListOf<Station>()
    features?.forEach {
        if (userLat in it.geometry.coordinates[1] - 1..it.geometry.coordinates[1] + 1
            && userLng in it.geometry.coordinates[0] - 1..it.geometry.coordinates[0] + 1
        ) {
            filteredList.add(it)
        }
    }
    return filteredList
}

fun List<Station>.getStationsClosestToUserLocation(userLat: Double, userLng: Double): List<Station> {
    val filteredList = mutableListOf<Station>()
    this?.forEach {
        if (userLat in it.geometry.coordinates[1] - 1..it.geometry.coordinates[1] + 1
            && userLng in it.geometry.coordinates[0] - 1..it.geometry.coordinates[0] + 1
        ) {
            filteredList.add(it)
        }
    }
    return filteredList
}

fun List<Station>.getOneStationClosestToUser(userLat: Double, userLng: Double): Station {
    var closestTotalDifference = 180.00
    var currentClosestStation = get(0)

    this.forEach {station ->
        station.properties.street?.let {
            val latDiff = abs(userLat - station.geometry.coordinates[1])
            val lngDiff = abs(userLng - station.geometry.coordinates[0])
            val totalDiff = latDiff + lngDiff
            if (totalDiff < closestTotalDifference) {
                closestTotalDifference = totalDiff
                currentClosestStation = station
            }
        }
    }
    return currentClosestStation
}

fun List<Station>.getTwoStationsClosestToUser(userLat: Double, userLng: Double, filterByChargerType: String? = null): List<Station> {
    var closestTotalDifference = 180.00
    val chargerTypeStations = this.filterByChargerType(filterByChargerType)
    val resultList = mutableListOf<Station>()
    if (chargerTypeStations.isNotEmpty()) {
        var currentClosestStation = chargerTypeStations.getOneStationClosestToUser(userLat, userLng)
        if (chargerTypeStations.size == 1) resultList.add(currentClosestStation) else {
            var secondClosestStation = currentClosestStation
            chargerTypeStations.forEach {station ->
                station.properties.street?.let {
                    val latDiff = abs(userLat - station.geometry.coordinates[1])
                    val lngDiff = abs(userLng - station.geometry.coordinates[0])
                    val totalDiff = latDiff + lngDiff
                    if (totalDiff < closestTotalDifference) {
                        closestTotalDifference = totalDiff
                        secondClosestStation = currentClosestStation
                        currentClosestStation = station
                    }
                }
            }
            resultList.add(currentClosestStation)
            resultList.add(secondClosestStation)
        }
    }

    return resultList
}

fun List<Station>.filterByChargerType(chargerType: String?): List<Station> {
    var keywords = mutableListOf<String>()
    when (chargerType) {
        ChargerTypesEnum.AC_TYPE_2.displayName -> {
            keywords.add("typ 2")
            keywords.add("typ2")
        }
        ChargerTypesEnum.AC_TYPE_1.displayName -> {
            keywords.add("Typ1")
            keywords.add("Typ 1")
        }
        ChargerTypesEnum.DC_EU.displayName -> {
            keywords.add("DC Kupplung Combo")
        }
        ChargerTypesEnum.DC_CHADEMO.displayName -> {
            keywords.add("chademo")
        }
        ChargerTypesEnum.TESLA.displayName -> {
            keywords.add("tesla")
        }
        else -> {}
    }
    return if (keywords.isEmpty()) this
    else this.filter { station ->
        var result = false
        if (station.properties.socket_type_list == null) result = true
        else {
            keywords.forEach { keyword ->
                station.properties.socket_type_list.forEach {
                    if (it.contains(keyword, true)) result = true
                }
            }
        }
        result
    }
}