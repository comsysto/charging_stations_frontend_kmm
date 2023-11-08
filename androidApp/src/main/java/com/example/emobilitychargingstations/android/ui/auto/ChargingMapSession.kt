package com.example.emobilitychargingstations.android.ui.auto

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.car.app.CarAppPermission
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.emobilitychargingstations.android.ui.auto.screen.ChargingMapScreen
import com.example.emobilitychargingstations.android.ui.auto.screen.EmptyScreen
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChargingMapSession(private val stationsRepo: StationsRepositoryImpl): Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        var stations: Stations?
        runBlocking {
            try {
                CarAppPermission.checkHasPermission(
                    carContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                CarAppPermission.checkHasPermission(
                    carContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } catch (exception: SecurityException) {
            //  TODO: implement permission handling
                Log.v("TEST LOCATION Exception", exception.toString())
            }
            stations = stationsRepo.getStationsLocal()
            if (stations == null) {
                val stationsJsonString = carContext.assets.open("munichStations.json").bufferedReader().use { it.readText() }
                val regensburgStationsJsonString = carContext.assets.open("regensburgStations.json").bufferedReader().use { it.readText() }
                var stationsFromJson = Json.decodeFromString<Stations>(stationsJsonString)
                val regensburgStationsFromJson = Json.decodeFromString<Stations>(regensburgStationsJsonString)
                val combinedStations = mutableListOf<Station>()
                stationsFromJson.features?.let { combinedStations.addAll(it) }
                regensburgStationsFromJson.features?.let { combinedStations.addAll(it) }
                stationsFromJson = stationsFromJson.copy(features = combinedStations)
                stationsRepo.insertStations(stationsFromJson)
                stations = stationsFromJson
            }
        }
        return stations?.let { ChargingMapScreen(carContext, it) } ?: EmptyScreen(carContext)
    }
}