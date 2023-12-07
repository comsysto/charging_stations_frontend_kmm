package com.example.emobilitychargingstations.data.extensions

import com.example.emobilitychargingstations.models.ChargerTypesEnum
import com.example.emobilitychargingstations.models.ChargingTypeEnum
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.StationGeoData
import com.example.emobilitychargingstations.models.UserLocation
import kotlin.math.abs

fun List<Station>.getStationsClosestToUserLocation(userLocation: UserLocation?): List<Station> {
    userLocation?.let {
        val filteredList = mutableListOf<Station>()
        this.forEach {
            if (userLocation.latitude in it.geometry.getLatitude() - 1..it.geometry.getLatitude() + 1
                && userLocation.longitude in it.geometry.getLongitude() - 1..it.geometry.getLongitude() + 1
            ) {
                filteredList.add(it)
            }
        }
        return filteredList
    }
    return this
}

fun List<Station>.getOneStationClosestToUser(userLat: Double, userLng: Double): Station {
    var closestTotalDifference = 180.00
    var currentClosestStation = get(0)

    this.forEach {station ->
        station.properties.street?.let {
            val latDiff = abs(userLat - station.geometry.getLatitude())
            val lngDiff = abs(userLng - station.geometry.getLongitude())
            val totalDiff = latDiff + lngDiff
            if (totalDiff < closestTotalDifference) {
                closestTotalDifference = totalDiff
                currentClosestStation = station
            }
        }
    }
    return currentClosestStation
}

fun List<Station>.getTwoStationsClosestToUser(userLat: Double, userLng: Double): List<Station> {
    var closestTotalDifference = 180.00
    val resultList = mutableListOf<Station>()
    if (this.isNotEmpty()) {
        var currentClosestStation = this.getOneStationClosestToUser(userLat, userLng)
        if (this.size == 1) resultList.add(currentClosestStation) else {
            var secondClosestStation = currentClosestStation
            this.forEach {station ->
                station.properties.street?.let {
                    val latDiff = abs(userLat - station.geometry.getLatitude())
                    val lngDiff = abs(userLng - station.geometry.getLongitude())
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

fun List<Station>.filterByChargerType(chargerType: ChargerTypesEnum?): List<Station> {
    val keywords = mutableListOf<String>()
    when (chargerType) {
        ChargerTypesEnum.AC_TYPE_2 -> {
            keywords.add("typ 2")
            keywords.add("typ2")
        }
        ChargerTypesEnum.AC_TYPE_1 -> {
            keywords.add("Typ1")
            keywords.add("Typ 1")
        }
        ChargerTypesEnum.DC_EU -> {
            keywords.add("DC Kupplung Combo")
        }
        ChargerTypesEnum.DC_CHADEMO -> {
            keywords.add("chademo")
        }
        ChargerTypesEnum.TESLA -> {
            keywords.add("tesla")
        }
        ChargerTypesEnum.ALL -> {
        }
        else -> {}
    }
    return if (keywords.isEmpty()) this
    else this.filter { station ->
        var result = false
//        if (station.properties.socket_type_list == null) result = true
//        else {
            keywords.forEach { keyword ->
                if (keyword == "tesla" && station.properties.operator?.contains(keyword, true) == true) result = true
                else station.properties.socket_type_list?.forEach {
                    if (it.contains(keyword, true)) result = true
                }
            }
//        }
        result
    }
}

fun List<Station>.filterByChargingType(chargingTypeEnum: ChargingTypeEnum): List<Station> {
    return filter { it.checkIsStationOfChargingType(chargingTypeEnum) }
}

fun Station.checkIsStationOfChargingType(chargingTypeEnum: ChargingTypeEnum): Boolean {
    var result = true
    this.properties.max_kw?.let {
        result = when (chargingTypeEnum) {
            ChargingTypeEnum.NORMAL -> {
                it <= 6.99
            }
            ChargingTypeEnum.FAST -> {
               it in 7.0 .. 42.99
            }
            ChargingTypeEnum.RAPID -> {
                it >= 43
            }
            ChargingTypeEnum.ANY -> {
                true
            }
        }
    }
    return result
}

fun StationGeoData.getLatitude() = coordinates[1]
fun StationGeoData.getLongitude() = coordinates[0]