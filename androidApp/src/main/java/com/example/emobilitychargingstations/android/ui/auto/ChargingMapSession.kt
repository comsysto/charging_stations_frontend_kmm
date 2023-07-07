package com.example.emobilitychargingstations.android.ui.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.emobilitychargingstations.android.ui.auto.screen.ChargingMapScreen
import com.example.emobilitychargingstations.android.ui.auto.screen.EmptyScreen
import com.example.emobilitychargingstations.domain.stations.Stations
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChargingMapSession(private val stationsRepo: StationsDataSourceImpl): Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        var stations: Stations? = null
        runBlocking {
            stations = stationsRepo.getAllStations()
            if (stations == null) {
                val stationsJsonString = carContext.assets.open("stationsData.json").bufferedReader().use { it.readText() }
                val stationsFromJson = Json.decodeFromString<Stations>(stationsJsonString)
                stationsRepo.insertStations(stationsFromJson)
                stations = stationsFromJson
            }
        }
        return stations?.let { ChargingMapScreen(carContext, it) } ?: EmptyScreen(carContext)
    }
}