package com.example.emobilitychargingstations.data.extensions

import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import kotlin.math.abs

fun Stations.getStationsClosestToUserLocation(userLat: Double, userLng: Double): List<Station> {
    val filteredList = mutableListOf<Station>()
    features?.forEach {
        if (userLat in it.geometry.coordinates[1] - 1..it.geometry.coordinates[1] + 1
            && userLng in it.geometry.coordinates[0] - 1..it.geometry.coordinates[0] + 1) {
            filteredList.add(it)
        }
    }
    return filteredList
}

fun List<Station>.getOneStationClosestToUser(userLat: Double, userLng: Double): Station {
    var closestTotalDifference = 180.00
    var currentClosestStation = get(0)

    this.forEach {
        val latDiff = abs(userLat - it.geometry.coordinates[1])
        val lngDiff = abs(userLng - it.geometry.coordinates[0])
        val totalDiff = latDiff + lngDiff
        if (totalDiff < closestTotalDifference) {
            closestTotalDifference = totalDiff
            currentClosestStation = it
        }
    }
    return currentClosestStation
}

fun List<Station>.getTwoStationsClosestToUser(userLat: Double, userLng: Double): List<Station> {
    var closestTotalDifference = 180.00
    var currentClosestStation = getOneStationClosestToUser(userLat, userLng)
    var secondClosestStation = currentClosestStation

    this.forEach {
        val latDiff = abs(userLat - it.geometry.coordinates[1])
        val lngDiff = abs(userLng - it.geometry.coordinates[0])
        val totalDiff = latDiff + lngDiff
        if (totalDiff < closestTotalDifference) {
            closestTotalDifference = totalDiff
            secondClosestStation = currentClosestStation
            currentClosestStation = it
        }
    }
    return listOf(currentClosestStation, secondClosestStation)
}