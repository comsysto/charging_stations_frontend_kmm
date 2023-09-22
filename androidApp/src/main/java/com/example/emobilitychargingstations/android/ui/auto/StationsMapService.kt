package com.example.emobilitychargingstations.android.ui.auto


import android.app.Notification
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import androidx.core.app.NotificationCompat
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class StationsMapService (): CarAppService() {
    @Inject
    lateinit var stationsDataSourceImpl: StationsDataSourceImpl


    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return ChargingMapSession(stationsDataSourceImpl)
    }

}