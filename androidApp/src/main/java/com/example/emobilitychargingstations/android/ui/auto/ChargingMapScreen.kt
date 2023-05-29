package com.example.emobilitychargingstations.android.ui.auto

import android.media.session.PlaybackState.CustomAction
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import com.comsystoreply.emobilitychargingstations.android.R

class ChargingMapScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val action = Action.BACK
        val customAction = Action.Builder().setTitle("Find Nearest").build()
        val actionStrip = ActionStrip.Builder().addAction(action).addAction(customAction).build()
        return NavigationTemplate.Builder()
//            .setMapActionStrip(actionStrip)
            .setActionStrip(actionStrip)
            .build()
    }
}