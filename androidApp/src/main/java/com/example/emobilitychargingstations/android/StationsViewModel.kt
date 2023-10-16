package com.example.emobilitychargingstations.android

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val stationsDataSource: StationsRepositoryImpl
) : ViewModel() {

    private val stationsData: MutableLiveData<Stations> = MutableLiveData()
    val _stationsData: LiveData<Stations> = stationsData

    private var userLocation = GeoPoint(51.3397, 12.3731)

    fun setUserLocation(newUserLocation: GeoPoint) {
        userLocation = newUserLocation
    }

    fun getUserLocation(): GeoPoint = userLocation
    fun getTestStations(context: Context) {
        viewModelScope.launch {
            val currentStations = stationsDataSource.getStationsLocal()
            if (currentStations?.features != null) {
                stationsData.value = currentStations
            }
            else {
                val stationsJsonString = context.assets.open("munichStations.json").bufferedReader().use { it.readText() }
                val stationObject = Json.decodeFromString<Stations>(stationsJsonString)
                stationsDataSource.insertStations(stationObject)
                stationsData.value = stationObject
            }
        }
    }
}