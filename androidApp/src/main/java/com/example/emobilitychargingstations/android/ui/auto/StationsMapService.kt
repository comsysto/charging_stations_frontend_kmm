package com.example.emobilitychargingstations.android.ui.auto

import android.content.Intent
import android.os.IBinder
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class StationsMapService: CarAppService() {
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return ChargingMapSession()
    }


}