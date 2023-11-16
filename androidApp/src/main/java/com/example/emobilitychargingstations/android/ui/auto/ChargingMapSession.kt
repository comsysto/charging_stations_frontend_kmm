package com.example.emobilitychargingstations.android.ui.auto

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.car.app.CarAppPermission
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.emobilitychargingstations.android.ui.auto.screen.ChargingMapScreen
import kotlinx.coroutines.runBlocking

class ChargingMapSession(): Session() {
    override fun onCreateScreen(intent: Intent): Screen {
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
            } catch (exception: SecurityException) {
            //  TODO: implement permission handling
                Log.v("TEST LOCATION Exception", exception.toString())
            }
        }
        return  ChargingMapScreen(carContext)
    }
}