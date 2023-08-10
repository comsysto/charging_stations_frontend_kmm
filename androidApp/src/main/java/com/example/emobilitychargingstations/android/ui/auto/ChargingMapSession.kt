package com.example.emobilitychargingstations.android.ui.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.emobilitychargingstations.android.ui.auto.screen.ChargingMapScreen
import com.example.emobilitychargingstations.android.ui.auto.screen.EmptyScreen
import com.example.emobilitychargingstations.data.extensions.getStationsClosestToUserLocation
import com.example.emobilitychargingstations.domain.stations.Station
import com.example.emobilitychargingstations.domain.stations.Stations
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.osmdroid.util.GeoPoint

class ChargingMapSession(private val stationsRepo: StationsDataSourceImpl): Session() {
    private val userLocation = GeoPoint(51.3397, 12.3731)
    override fun onCreateScreen(intent: Intent): Screen {
        var stations: Stations?
        var stationList: List<Station>
        runBlocking {
            stations = stationsRepo.getAllStations()
            if (stations == null) {
                val stationsJsonString = carContext.assets.open("stationsData.json").bufferedReader().use { it.readText() }
                val stationsFromJson = Json.decodeFromString<Stations>(stationsJsonString)
                stationsRepo.insertStations(stationsFromJson)
                stations = stationsFromJson
            }
            stationList = stations!!.getStationsClosestToUserLocation(userLocation.latitude, userLocation.longitude)
        }
        return stations?.let { ChargingMapScreen(carContext, stationList) } ?: EmptyScreen(carContext)
    }
}