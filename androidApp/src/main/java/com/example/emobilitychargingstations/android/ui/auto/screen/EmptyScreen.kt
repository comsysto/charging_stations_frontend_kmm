package com.example.emobilitychargingstations.android.ui.auto.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Distance
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.RoutingInfo
import androidx.car.app.navigation.model.Step

class EmptyScreen(carContext: CarContext): Screen(carContext) {

    override fun onGetTemplate(): Template {
        val action = Action.BACK
        val customAction = Action.Builder().setTitle("test").build()
        val actionStrip = ActionStrip.Builder().addAction(action).addAction(customAction).build()
        return NavigationTemplate.Builder()
            .setNavigationInfo(
                RoutingInfo.Builder()
                .setCurrentStep(
                    Step.Builder().build(),
                    Distance.create(1.0, Distance.UNIT_KILOMETERS_P1)
                )
                .setNextStep(Step.Builder().build())
                .build()
            )
//            .setMapActionStrip(actionStrip)
            .setActionStrip(actionStrip)
            .build()
    }
}