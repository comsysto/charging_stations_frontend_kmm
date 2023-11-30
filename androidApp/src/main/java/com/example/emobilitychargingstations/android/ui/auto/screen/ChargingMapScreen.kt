package com.example.emobilitychargingstations.android.ui.auto.screen

import android.annotation.SuppressLint
import android.location.Location
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import androidx.car.app.CarContext
import androidx.car.app.OnScreenResultListener
import androidx.car.app.model.*
import androidx.car.app.model.Distance.UNIT_KILOMETERS_P1
import androidx.car.app.model.Distance.create
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.comsystoreply.emobilitychargingstations.android.BuildConfig
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.BaseScreen
import com.example.emobilitychargingstations.android.ui.utilities.buildRowWithPlace
import com.example.emobilitychargingstations.android.ui.utilities.createCarIconFromBitmap
import com.example.emobilitychargingstations.android.ui.utilities.getPlaceWithMarker
import com.example.emobilitychargingstations.android.ui.utilities.getString
import com.example.emobilitychargingstations.android.ui.utilities.AUTO_POI_MAP_SCREEN_MARKER
import com.example.emobilitychargingstations.android.ui.utilities.LocationRequestStarter
import com.example.emobilitychargingstations.android.ui.utilities.NAVIGATION_DISTANCE_VALUE_FOR_COMPLETION_IN_METERS
import com.example.emobilitychargingstations.android.ui.utilities.getTransparentCarColor
import com.example.emobilitychargingstations.data.extensions.getTwoStationsClosestToUser
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.StationGeoData
import com.example.emobilitychargingstations.models.UserLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SuppressLint("MissingPermission")
class ChargingMapScreen(carContext: CarContext) : BaseScreen(carContext), OnScreenResultListener {

    private var initialUserLocation: UserLocation? = null
    private var closestStations: List<Station> = listOf()
    private var stationToNavigateTo: Station? = null
    private var initialStationList: List<Station>? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.firstOrNull()?.let {
                if (checkIsLocationMockDebug(it)) {
                    if (stationToNavigateTo != null && getDistanceValue(it, stationToNavigateTo!!.geometry) < NAVIGATION_DISTANCE_VALUE_FOR_COMPLETION_IN_METERS) pushDestinationReachedScreen(stationToNavigateTo!!)
                    else {
                        val userLocation = UserLocation(it.latitude, it.longitude)
                        if (initialUserLocation == null) startStationsRepeatingRequest(userLocation)
                        initialUserLocation = userLocation
                        stationsUseCase.setTemporaryLocation(initialUserLocation)
                        filterStations()
                        invalidate()
                    }
                }
            }
        }
    }

    init {
        LocationRequestStarter(carContext, locationCallback)
        marker = AUTO_POI_MAP_SCREEN_MARKER
    }

    override fun onGetTemplate(): Template {
        val mapTemplateBuilder = fillMapTemplateBuilder()
        return mapTemplateBuilder.build()
    }

    override fun onScreenResult(result: Any?) {
        result?.let {stationResult ->
            val station = stationResult as Station
            if (station.isNavigatingTo) {
                stationToNavigateTo = station
                closestStations = listOf(station)
            }
            else {
                stationToNavigateTo = null
                closestStations.forEach { it.isNavigatingTo = false }
                filterStations()
            }
            invalidate()
        }
    }

    private fun startStationsRepeatingRequest (userLocation: UserLocation) {
        stationsUseCase.startRepeatingRequest(
            userLocation
        ).onEach {
            if (stationToNavigateTo == null && it != initialStationList) {
                initialStationList = it
                filterStations()
                invalidate()
            }
        }.launchIn(lifecycleScope)
    }

    private fun pushDestinationReachedScreen(station: Station) {
        closestStations.firstOrNull()?.isNavigatingTo = false
        stationToNavigateTo = null
        screenManager.push(NavigationCompleteScreen(carContext, station))
    }

    private fun getDistanceValue(location: Location, stationLocation: StationGeoData): Float {
        val distanceResult: FloatArray = floatArrayOf(0.0f)
        Location.distanceBetween(
            location.latitude,
            location.longitude,
            stationLocation.coordinates[1],
            stationLocation.coordinates[0],
            distanceResult
        )
        return distanceResult[0]
    }

    private fun filterStations() {
        if (stationToNavigateTo == null) {
            closestStations = if (initialStationList.isNullOrEmpty() || initialUserLocation == null) listOf()
            else initialStationList!!.getTwoStationsClosestToUser(initialUserLocation!!.latitude, initialUserLocation!!.longitude)
        }
    }

    private fun checkIsLocationMockDebug(location: Location) : Boolean {
        return if (BuildConfig.DEBUG) location.isMock else true
//        return true
    }

    private fun fillMapTemplateBuilder(): PlaceListMapTemplate.Builder {
        val actionStrip = ActionStrip.Builder().addAction(createFavoritesAction()).build()
        val mapTemplateBuilder = PlaceListMapTemplate.Builder().setActionStrip(actionStrip)
        val carIcon = carContext.getDrawable(R.drawable.electric_car_icon)?.toBitmap()
        var firstStation: Station? = null
        var secondStation: Station? = null
        var firstItemIcon = carIcon
        var firstItemTitle: SpannableString? = null
        var secondItemTitle: SpannableString? = null
        mapTemplateBuilder.setTitle(getString(R.string.auto_map_title))
        if (initialUserLocation == null || initialStationList == null) return mapTemplateBuilder.setLoading(
            true
        )
        mapTemplateBuilder.setAnchor(
            getPlaceWithMarker(
                initialUserLocation!!.latitude,
                initialUserLocation!!.longitude,
                CarColor.PRIMARY
            )
        )
        if (closestStations.size == 1) {
            firstStation = closestStations.first()
            firstItemTitle = SpannableString("${firstStation.properties.street} - ")
            if (stationToNavigateTo != null) {
                firstStation = stationToNavigateTo!!
                firstItemIcon = carContext.getDrawable(R.drawable.navigating_to_icon)?.toBitmap()
                mapTemplateBuilder.setTitle(getString(R.string.auto_map_navigation_title))
            }
        } else if (closestStations.size > 1) {
            firstStation = closestStations.first()
            secondStation = closestStations[1]
            firstItemTitle = SpannableString("${firstStation.properties.street} - ")
            secondItemTitle = SpannableString("${secondStation.properties.street} - ")
        }
        calculateDistanceAndGetTitles(firstItemTitle, secondItemTitle, firstStation, secondStation)
        if (closestStations.isNotEmpty()) mapTemplateBuilder.apply {
            setItemList(
                ItemList.Builder().apply {
                    firstStation?.let {
                        addItem(
                            buildRowWithPlace(
                                firstItemTitle!!, getPlaceWithMarker(
                                    it.geometry.coordinates[1],
                                    it.geometry.coordinates[0],
                                    if (stationToNavigateTo != null) CarColor.GREEN else getTransparentCarColor(),
                                    firstItemIcon
                                )
                            ) {
                                onItemClick(it)
                            })
                    }
                    secondStation?.let {
                        addItem(
                            buildRowWithPlace(
                                secondItemTitle!!, getPlaceWithMarker(
                                    it.geometry.coordinates[1],
                                    it.geometry.coordinates[0],
                                    getTransparentCarColor(),
                                    carIcon
                                )
                            ) {
                                onItemClick(it)
                            }
                        )
                    }
                }.build()
            )
        } else mapTemplateBuilder.setItemList(
            ItemList.Builder().setNoItemsMessage(getString(R.string.auto_map_empty_list_message))
                .build()
        )
        return mapTemplateBuilder
    }

    private fun createFavoritesAction() = Action.Builder().apply {
        setIcon(
            createCarIconFromBitmap(
                carContext.getDrawable(R.drawable.favorites_star_icon)!!.toBitmap())
        )
        setOnClickListener(ParkedOnlyOnClickListener.create {
            screenManager.push(FavoritesListScreen(carContext, this@ChargingMapScreen))
        })
    }.build()

    private fun calculateDistanceAndGetTitles(
        firstItemTitle: SpannableString?,
        secondItemTitle: SpannableString?,
        firstStation: Station?,
        secondStation: Station?
    ) {
        val distanceResult: FloatArray = floatArrayOf(0.0f)
        firstStation?.let {
            Location.distanceBetween(
                initialUserLocation!!.latitude,
                initialUserLocation!!.longitude,
                it.geometry.coordinates[1],
                it.geometry.coordinates[0],
                distanceResult
            )
            firstItemTitle?.setSpan(
                DistanceSpan.create(create(distanceResult[0] / 1000.toDouble(), UNIT_KILOMETERS_P1)),
                firstItemTitle.length,
                firstItemTitle.length,
                SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        secondStation?.let {
            Location.distanceBetween(
                initialUserLocation!!.latitude,
                initialUserLocation!!.longitude,
                it.geometry.coordinates[1],
                it.geometry.coordinates[0],
                distanceResult
            )
            secondItemTitle?.setSpan(
                DistanceSpan.create(create(distanceResult[0] / 1000.toDouble(), UNIT_KILOMETERS_P1)),
                secondItemTitle.length,
                secondItemTitle.length,
                SPAN_INCLUSIVE_INCLUSIVE
            )
        }

    }

    private fun onItemClick(station: Station) {
            screenManager.pushForResult(StationDetailsScreen(carContext, station = station), this)
    }

}

