package com.example.emobilitychargingstations.android.ui.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import com.example.emobilitychargingstations.android.di.EntryPoints
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import dagger.hilt.android.EntryPointAccessors

open class BaseScreen(carContext: CarContext): Screen(carContext) {

    var stationsRepo: StationsRepositoryImpl

    init {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(carContext.applicationContext, EntryPoints.ScreenEntryPoint::class.java)
        stationsRepo = hiltEntryPoint.stationsRepo()
    }
    override fun onGetTemplate(): Template {
        TODO("Not yet implemented")
    }
}