package com.example.emobilitychargingstations.android.ui.auto


import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.example.emobilitychargingstations.data.stations.StationsRepositoryImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class StationsMapService (): CarAppService() {
    @Inject
    lateinit var stationsRepositoryImpl: StationsRepositoryImpl


    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return ChargingMapSession(stationsRepositoryImpl)
    }

}