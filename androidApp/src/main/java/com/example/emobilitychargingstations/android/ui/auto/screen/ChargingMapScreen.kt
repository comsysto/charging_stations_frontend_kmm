package com.example.emobilitychargingstations.android.ui.auto.screen

import android.location.Location
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.model.Distance.UNIT_KILOMETERS_P1
import androidx.car.app.model.Distance.create
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.extensions.getPlaceWithMarker
import com.example.emobilitychargingstations.android.ui.auto.extensions.buildRowWithPlace
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString
import com.example.emobilitychargingstations.data.extensions.getTwoStationsClosestToUser
import com.example.emobilitychargingstations.domain.stations.Station
import org.osmdroid.util.GeoPoint

class ChargingMapScreen(carContext: CarContext, stationsList: List<Station>, val userLocation: GeoPoint) : Screen(carContext) {

    private var closestStations: List<Station> = if (stationsList.isEmpty()) listOf()
        else stationsList.getTwoStationsClosestToUser(userLocation.latitude, userLocation.longitude)

    override fun onGetTemplate(): Template {
        val action = Action.BACK
        val mapTitle = getString(R.string.auto_map_title)
        val actionStrip = ActionStrip.Builder().addAction(action).build()
        val mapTemplate: PlaceListMapTemplate = if (closestStations.isEmpty()) PlaceListMapTemplate.Builder()
                .setTitle(mapTitle)
                .setAnchor(
                    getPlaceWithMarker(
                        userLocation.latitude,
                        userLocation.longitude,
                        CarColor.PRIMARY
                    )
                )
            .setItemList(
                ItemList.Builder().setNoItemsMessage(getString(R.string.auto_map_empty_list_message)).build()
            )
                .setActionStrip(actionStrip)
                .build()
         else {
            val firstItemTitle = SpannableString("${closestStations[0].properties.street} ")
            val secondItemTitle = SpannableString("${closestStations[1].properties.street} ")
            calculateDistanceAndGetTitles(firstItemTitle, secondItemTitle, closestStations)
            PlaceListMapTemplate.Builder()
                .setTitle(mapTitle)
                .setAnchor(
                    getPlaceWithMarker(
                        userLocation.latitude,
                        userLocation.longitude,
                        CarColor.PRIMARY
                    )
                )
                .setItemList(
                    ItemList.Builder()
                        .addItem(
                            buildRowWithPlace(
                                firstItemTitle, getPlaceWithMarker(
                                    closestStations[0].geometry.coordinates[1],
                                    closestStations[0].geometry.coordinates[0],
                                    CarColor.createCustom(
                                        Color.Transparent.hashCode(),
                                        Color.Transparent.hashCode()
                                    ),
                                    carContext.getDrawable(R.drawable.electric_car_icon)?.toBitmap()
                                )
                            ) {
                                onItemClick(true)
                            })
                        .addItem(
                            buildRowWithPlace(
                                secondItemTitle, getPlaceWithMarker(
                                    closestStations[1].geometry.coordinates[1],
                                    closestStations[1].geometry.coordinates[0],
                                    CarColor.createCustom(
                                        Color.Transparent.hashCode(),
                                        Color.Transparent.hashCode()
                                    ),
                                    carContext.getDrawable(R.drawable.electric_car_icon)?.toBitmap()
                                )
                            ) {
                                onItemClick(false)
                            }
                        )
                        .build())
                .setActionStrip(actionStrip)
                .build()
        }
        return mapTemplate
    }

    private fun calculateDistanceAndGetTitles(
        firstItemTitle: SpannableString,
        secondItemTitle: SpannableString,
        closestStations: List<Station>
    ) {
        val distanceResult: FloatArray = floatArrayOf(0.0f)
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            closestStations[0].geometry.coordinates[1],
            closestStations[0].geometry.coordinates[0],
            distanceResult
        )
        firstItemTitle.setSpan(
            DistanceSpan.create(create(distanceResult[0] / 1000.toDouble(), UNIT_KILOMETERS_P1)),
            firstItemTitle.length,
            firstItemTitle.length,
            SPAN_INCLUSIVE_INCLUSIVE
        )
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            closestStations[1].geometry.coordinates[1],
            closestStations[1].geometry.coordinates[0],
            distanceResult
        )
        secondItemTitle.setSpan(
            DistanceSpan.create(create(distanceResult[0] / 1000.toDouble(), UNIT_KILOMETERS_P1)),
            secondItemTitle.length,
            secondItemTitle.length,
            SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun onItemClick(isClosestClick: Boolean) {
        val station = if (isClosestClick) closestStations[0] else closestStations[1]
        screenManager.push(
            StationDetailsScreen(carContext, station = station)
        )
    }
}

