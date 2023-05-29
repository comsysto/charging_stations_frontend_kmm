package com.example.emobilitychargingstations.android.ui.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class ChargingMapSession: Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return ChargingMapScreen(carContext)
    }
}