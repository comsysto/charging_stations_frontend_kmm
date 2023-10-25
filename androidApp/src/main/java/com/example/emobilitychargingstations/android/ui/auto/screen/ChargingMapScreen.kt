package com.example.emobilitychargingstations.android.ui.auto.screen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.model.Distance.UNIT_KILOMETERS_P1
import androidx.car.app.model.Distance.create
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.comsystoreply.emobilitychargingstations.android.BuildConfig
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.extensions.getPlaceWithMarker
import com.example.emobilitychargingstations.android.ui.auto.extensions.buildRowWithPlace
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString
import com.example.emobilitychargingstations.data.extensions.getStationsClosestToUserLocation
import com.example.emobilitychargingstations.data.extensions.getTwoStationsClosestToUser
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class ChargingMapScreen(carContext: CarContext, val stationsList: Stations, val stationsRepo: StationsRepositoryImpl) : Screen(carContext), LocationListener {

    private var userInfo = stationsRepo.getUserInfo()
    private val locationManager: LocationManager = carContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var initialUserLocation: UserLocation? = null
    private var closestStations: List<Station> = listOf()
    private val locationRequest =
        LocationRequest.Builder(4000).apply {
            setMinUpdateDistanceMeters(100f)
        }.build()

    override fun onLocationChanged(location: Location) {
        location?.let {
            if (checkIsLocationMockDebug(location)) {
                initialUserLocation = UserLocation(it.latitude, it.longitude)
                filterStations()
                invalidate()
            }
        }
    }

    private fun filterStations() {
        closestStations = if (stationsList.features.isNullOrEmpty() || initialUserLocation == null) listOf()
        else stationsList.getStationsClosestToUserLocation(initialUserLocation!!.latitude, initialUserLocation!!.longitude).getTwoStationsClosestToUser(initialUserLocation!!.latitude, initialUserLocation!!.longitude, userInfo?.chargerType)
    }

    private fun checkIsLocationMockDebug(location: Location) : Boolean {
        return if (BuildConfig.DEBUG) location.isMock else true
    }
    init {
        locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)?.let {
            if (it.isMock || initialUserLocation == null) {
                initialUserLocation = UserLocation(it.latitude, it.longitude)
                filterStations()
                invalidate()
            }
        }
        if (initialUserLocation == null) locationManager.getCurrentLocation(LocationManager.FUSED_PROVIDER, null, carContext.mainExecutor) {
            it?.let {
                initialUserLocation = UserLocation(it.latitude, it.longitude)
                filterStations()
                invalidate()
            }
        }
        locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, locationRequest, carContext.mainExecutor, this)
    }



    override fun onGetTemplate(): Template {
        lifecycleScope.launch {
            stationsRepo.getUserInfoAsFlow().onEach {
                if (it?.chargerType != userInfo?.chargerType) {
                    userInfo = it
                    filterStations()
                    invalidate()
                }
            }.collect()
        }

        val getUserLocationAction = Action.Builder().setIcon(CarIcon.APP_ICON).setOnClickListener {
        }.build()
        val action = Action.BACK
        val mapTitle = getString(R.string.auto_map_title)
        val actionStrip = ActionStrip.Builder().addAction(getUserLocationAction).build()
        val mapTemplateBuilder = PlaceListMapTemplate.Builder().setTitle(mapTitle).setActionStrip(actionStrip)
        if (initialUserLocation != null) mapTemplateBuilder.setAnchor(
            getPlaceWithMarker(
                initialUserLocation!!.latitude,
                initialUserLocation!!.longitude,
                CarColor.PRIMARY
            )
        )
        if (initialUserLocation == null) mapTemplateBuilder.setLoading(true)
        else if (closestStations.isEmpty()) mapTemplateBuilder.setItemList(
            ItemList.Builder().setNoItemsMessage(getString(R.string.auto_map_empty_list_message)).build()
        ) else {
            val firstItemTitle = SpannableString("${closestStations[0].properties.street} ")
            val secondItemTitle = SpannableString("${closestStations[1].properties.street} ")
            calculateDistanceAndGetTitles(firstItemTitle, secondItemTitle, closestStations)
            mapTemplateBuilder
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
        }
        return mapTemplateBuilder.build()
    }

    private fun calculateDistanceAndGetTitles(
        firstItemTitle: SpannableString,
        secondItemTitle: SpannableString,
        closestStations: List<Station>
    ) {
        val distanceResult: FloatArray = floatArrayOf(0.0f)
        Location.distanceBetween(
            initialUserLocation!!.latitude,
            initialUserLocation!!.longitude,
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
            initialUserLocation!!.latitude,
            initialUserLocation!!.longitude,
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
        if (closestStations.isNotEmpty()) {
            val station = if (isClosestClick) closestStations[0] else closestStations[1]
            screenManager.push(
                StationDetailsScreen(carContext, station = station)
            )
        }
    }


}

