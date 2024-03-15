package com.example.emobilitychargingstations.android.ui.auto

import android.Manifest
import android.content.Intent
import androidx.car.app.CarAppPermission
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.emobilitychargingstations.android.ui.auto.screen.ChargingMapScreen
import com.example.emobilitychargingstations.android.ui.auto.screen.PermissionScreen

class ChargingMapSession: Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        val showPermissionsScreen = try {
            CarAppPermission.checkHasPermission(
                carContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            CarAppPermission.checkHasPermission(
                carContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            false
        } catch (exception: SecurityException) {
            true
        }
        return if (showPermissionsScreen) PermissionScreen(carContext) else ChargingMapScreen(carContext)
    }
}