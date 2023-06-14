package com.example.emobilitychargingstations.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emobilitychargingstations.domain.stations.Stations
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val stationsDataSource: StationsDataSourceImpl
) : ViewModel() {

    private val stationsData: MutableLiveData<Stations> = MutableLiveData()
    val _stationsData: LiveData<Stations> = stationsData

    fun getTestStations(jsonString: String) {
        viewModelScope.launch {
            val currentStations = stationsDataSource.getAllStations()
            if (currentStations?.features != null) stationsData.value = currentStations
            else {
                val stationObject = Json.decodeFromString<Stations>(jsonString)
                stationsDataSource.insertStations(stationObject)
                stationsData.value = stationObject
            }
        }
    }
}