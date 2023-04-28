package com.example.emobilitychargingstations.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emobilitychargingstations.domain.stations.Station
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val stationsDataSource: StationsDataSourceImpl
): ViewModel() {

    private val stationsData: MutableLiveData<Station> = MutableLiveData()
    val _stationsData: LiveData<Station> = stationsData

    init {
        viewModelScope.launch {
            stationsDataSource.insertTestStation()
        }
    }

    fun getTestStations() {
        viewModelScope.launch {
           stationsData.value = stationsDataSource.getAllStations()[0]
        }
    }
}