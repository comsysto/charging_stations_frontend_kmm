package com.example.emobilitychargingstations.android.ui.auto


import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.example.emobilitychargingstations.domain.stations.StationsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class StationsMapService (): CarAppService() {
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return ChargingMapSession()
    }

}