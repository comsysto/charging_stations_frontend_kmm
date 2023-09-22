package com.example.emobilitychargingstations.android.ui.auto.screen

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import com.example.emobilitychargingstations.android.ui.auto.extensions.buildRowWithText
import com.example.emobilitychargingstations.domain.stations.Station

class StationDetailsScreen(carContext: CarContext, val station: Station) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val stationsPane = Pane.Builder()
            .addAction(
                Action.Builder().setTitle("Start navigation")
                    .setBackgroundColor(CarColor.GREEN)
                    .setOnClickListener(this::startNavigation).build()
            )
            .addRow(
                buildRowWithText(
                    title = SpannableString("StationId:"),
                    text = "${station.properties.station_id}"
                )
            )
            .build()
        return PaneTemplate.Builder(stationsPane).setTitle("${station.properties.operator}")
            .setHeaderAction(Action.BACK)
            .build()
    }

    fun startNavigation() {
        val latitude = station.geometry.coordinates[1]
        val longitude = station.geometry.coordinates[0]
        val name = "Google navigation test"
        val intent = Intent(CarContext.ACTION_NAVIGATE, Uri.parse("geo:0,0?q=${latitude},${longitude}(${name})"))
        intent.`package` = "com.google.android.apps.maps"
        carContext.startCarApp(intent)
        screenManager.pop()
//        screenManager.push(
//            NavigationMapScreen(
//                carContext,
//                if (isClosestClick) closestStations[0] else closestStations[1]
//            )
//        )
    }

}