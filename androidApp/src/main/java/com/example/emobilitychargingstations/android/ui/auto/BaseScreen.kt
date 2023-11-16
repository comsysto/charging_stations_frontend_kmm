package com.example.emobilitychargingstations.android.ui.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import com.example.emobilitychargingstations.domain.stations.StationsUseCase
import com.example.emobilitychargingstations.domain.user.UserUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseScreen(carContext: CarContext): Screen(carContext), KoinComponent {

    val userUseCase by inject<UserUseCase> ()
    val stationsUseCase by inject<StationsUseCase> ()

    abstract override fun onGetTemplate(): Template
}