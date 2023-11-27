package com.example.emobilitychargingstations.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emobilitychargingstations.domain.stations.StationsUseCase
import com.example.emobilitychargingstations.domain.user.UserUseCase
import com.example.emobilitychargingstations.models.ChargerTypesEnum
import com.example.emobilitychargingstations.models.ChargingTypeEnum
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.UserInfo
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint


class StationsViewModel(
    private val userUseCase: UserUseCase,
    private val stationsUseCase: StationsUseCase
) : ViewModel() {

    private val stationsData: MutableLiveData<List<Station>> = MutableLiveData()
    val _stationsData: LiveData<List<Station>> = stationsData

    private val userLocation : MutableLiveData<GeoPoint> = MutableLiveData()
    val _userLocation: LiveData<GeoPoint> = userLocation

    private var stationsJob: Job? = null

    fun setUserLocation(newUserLocation: GeoPoint) {
        stationsUseCase.setTemporaryLocation(UserLocation(newUserLocation.latitude, newUserLocation.longitude))
        userLocation.value = newUserLocation
    }
    fun startRepeatingStationsRequest() {
        if (stationsJob == null) stationsJob = stationsUseCase.startRepeatingRequest(UserLocation(userLocation.value?.latitude ?: 0.0, userLocation.value?.longitude ?: 0.0)).onEach {
            if (it != null && it != stationsData.value) {
                stationsData.postValue(it)
            }
        }.launchIn(viewModelScope)
    }

    fun stopRepeatingStationsRequest() {
        stationsJob?.cancel()
        stationsJob = null
    }

    fun setChargerType(chargerName: ChargerTypesEnum) {
        viewModelScope.launch {
            userUseCase.setChargerType(chargerName)
        }
    }

    fun setChargingType(chargingType: ChargingTypeEnum) {
        viewModelScope.launch {
            userUseCase.setChargingType(chargingType)
        }
    }

    fun getUserInfo(): UserInfo? = userUseCase.getUserInfo()

}