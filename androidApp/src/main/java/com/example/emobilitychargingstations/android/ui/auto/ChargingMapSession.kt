package com.example.emobilitychargingstations.android.ui.auto

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.car.app.CarAppPermission
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.emobilitychargingstations.android.ui.auto.screen.ChargingMapScreen

class ChargingMapSession(): Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        //  TODO: implement proper permission handling
            try {
                CarAppPermission.checkHasPermission(
                    carContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                CarAppPermission.checkHasPermission(
                    carContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } catch (exception: SecurityException) {
                Log.v("TEST LOCATION Exception", exception.toString())
            }
        return  ChargingMapScreen(carContext)
    }
}