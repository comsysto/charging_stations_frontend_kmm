package com.example.emobilitychargingstations.android.ui.auto

import android.location.Location
import android.media.session.PlaybackState.CustomAction
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.SurfaceCallback
import androidx.car.app.model.*
import androidx.car.app.model.Distance.UNIT_KILOMETERS_P1
import androidx.car.app.model.Distance.create
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.NavigationTemplate.NavigationInfo
import androidx.car.app.navigation.model.RoutingInfo
import androidx.car.app.navigation.model.Step
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.models.StationItem
import com.example.emobilitychargingstations.domain.stations.StationGeoData
import com.example.emobilitychargingstations.domain.stations.StationProperties

class ChargingMapScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
//        carContext.getCarService(AppManager::class.java)
//            .setSurfaceCallback(object : SurfaceCallback {
//
//            })
        val action = Action.BACK
        val customAction = Action.Builder().setTitle("Find Nearest").build()
        val actionStrip = ActionStrip.Builder().addAction(action).addAction(customAction).build()
        val navigationTemplate = NavigationTemplate.Builder()
            .setNavigationInfo(RoutingInfo.Builder()
                .setCurrentStep(Step.Builder().build(), create(1.0, UNIT_KILOMETERS_P1))
                .setNextStep(Step.Builder().build())
                .build()
            )
//            .setMapActionStrip(actionStrip)
            .setActionStrip(actionStrip)
            .build()
        val spannableString = SpannableString("testing!")
        spannableString.setSpan(DistanceSpan.create(Distance.create(1.0, UNIT_KILOMETERS_P1)), 0, 1, SPAN_INCLUSIVE_INCLUSIVE)
        val mapTemplate = PlaceListMapTemplate.Builder()
            .setTitle("TEST THE MAP")
            .setAnchor(Place.Builder(CarLocation.create(51.3397, 12.3731)).build())
            .setItemList(
                ItemList.Builder()
                    .addItem(Row.Builder().setBrowsable(false).setTitle(spannableString).setOnClickListener {  }
                        .build())
                    .addItem(Row.Builder().setBrowsable(false).setTitle(spannableString).build())
                    .build())
            .setActionStrip(actionStrip)
            .build()
        return mapTemplate
    }

    fun getTestStationItem(): StationItem {
        return StationItem(
            1353151,
            "sdg", StationProperties("", "", 12212, "", ""),
            StationGeoData("Test", arrayOf(51.3397, 12.3731))
        )
    }

    fun getTestStationItem2(): StationItem {
        return StationItem(
            1353151,
            "sdg", StationProperties("", "", 12212, "", ""),
            StationGeoData("Test", arrayOf(51.2397, 12.2731))
        )
    }
}

