package com.example.emobilitychargingstations.android.ui.auto

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import androidx.car.app.CarAppPermission
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

    private var userLocation = GeoPoint(51.3397, 12.3731)
    override fun onCreateScreen(intent: Intent): Screen {
        var stations: Stations?
        var stationList: List<Station>
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
                val locationManager: LocationManager = carContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) userLocation = GeoPoint(location.latitude, location.longitude)
            } catch (exception: SecurityException) {
                Log.v("TEST LOCATION Exception", exception.toString())
            }
            stations = stationsRepo.getAllStations()
            if (stations == null) {
                val stationsJsonString = carContext.assets.open("munichStations.json").bufferedReader().use { it.readText() }
                val stationsFromJson = Json.decodeFromString<Stations>(stationsJsonString)
                stationsRepo.insertStations(stationsFromJson)
                stations = stationsFromJson
            }
            stationList = stations!!.getStationsClosestToUserLocation(userLocation.latitude, userLocation.longitude)
        }
        return stations?.let { ChargingMapScreen(carContext, stationList, userLocation) } ?: EmptyScreen(carContext)
    }
}