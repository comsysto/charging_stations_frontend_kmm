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
import androidx.compose.ui.graphics.Color
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
import com.example.emobilitychargingstations.data.extensions.getStationsClosestToUserLocation
import com.example.emobilitychargingstations.data.extensions.getTwoStationsClosestToUser
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.StationGeoData
import com.example.emobilitychargingstations.models.UserLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class ChargingMapScreen(carContext: CarContext) : BaseScreen(carContext), OnScreenResultListener {

    private var userInfo = userUseCase.getUserInfo()
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
                        initialUserLocation = UserLocation(it.latitude, it.longitude)
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
        stationsUseCase.startRepeatingRequest(initialUserLocation).onEach {
            if (it != initialStationList) {
                initialStationList = it
                filterStations()
                invalidate()
            }
        }.launchIn(lifecycleScope)
        marker = AUTO_POI_MAP_SCREEN_MARKER
    }

    private fun pushDestinationReachedScreen(station: Station) {
        closestStations[0].isNavigatingTo = false
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

    override fun onScreenResult(result: Any?) {
        result?.let {stationResult ->
            val station = stationResult as Station
            if (station.isNavigatingTo) {
                stationToNavigateTo = station
                closestStations = listOf(station, closestStations[1])
            }
            else {
                stationToNavigateTo = null
                closestStations.forEach { it.isNavigatingTo = false }
                filterStations()
            }
        }
    }

    private fun filterStations() {
        closestStations = if (initialStationList.isNullOrEmpty() || initialUserLocation == null) listOf()
        else initialStationList!!.getStationsClosestToUserLocation(initialUserLocation!!.latitude, initialUserLocation!!.longitude).getTwoStationsClosestToUser(initialUserLocation!!.latitude, initialUserLocation!!.longitude, userInfo?.filterProperties?.chargerType)
    }

    private fun checkIsLocationMockDebug(location: Location) : Boolean {
        return if (BuildConfig.DEBUG) location.isMock else true
//        return true
    }

    override fun onGetTemplate(): Template {
        lifecycleScope.launch {
            userUseCase.getUserInfoAsFlow().onEach {
                if (it?.filterProperties?.chargerType != userInfo?.filterProperties?.chargerType) {
                    userInfo = it
                    filterStations()
                    invalidate()
                }
            }.collect()
        }
        val openFavoritesListAction = Action.Builder().apply {
            setIcon(
                createCarIconFromBitmap(
                carContext.getDrawable(R.drawable.favorites_star_icon)!!.toBitmap())
            )
            setOnClickListener(ParkedOnlyOnClickListener.create {
            screenManager.push(FavoritesListScreen(carContext, this@ChargingMapScreen))
        })
        }.build()
        var mapTitle = getString(R.string.auto_map_title)
        val actionStrip = ActionStrip.Builder().addAction(openFavoritesListAction).build()
        val mapTemplateBuilder = PlaceListMapTemplate.Builder().setActionStrip(actionStrip)
        if (initialUserLocation == null || initialStationList == null) mapTemplateBuilder.setLoading(true)
        else {
            mapTemplateBuilder.setAnchor(
                getPlaceWithMarker(
                    initialUserLocation!!.latitude,
                    initialUserLocation!!.longitude,
                    CarColor.PRIMARY
                )
            )
            if (closestStations.isEmpty()) mapTemplateBuilder.setItemList(
                ItemList.Builder()
                    .setNoItemsMessage(getString(R.string.auto_map_empty_list_message)).build()
            ) else {
                var firstStation = closestStations[0]
                var secondStation: Station? = closestStations[1]
                var firstItemIcon = carContext.getDrawable(R.drawable.electric_car_icon)?.toBitmap()
                if (stationToNavigateTo != null) {
                    mapTitle = getString(R.string.auto_map_navigation_title)
                    firstStation = stationToNavigateTo!!
                    secondStation = null
                    firstItemIcon =
                        carContext.getDrawable(R.drawable.navigating_to_icon)?.toBitmap()
                }
                var firstItemTitle = SpannableString("${firstStation.properties.street} - ")
                var secondItemTitle = SpannableString("${secondStation?.properties?.street} - ")
                calculateDistanceAndGetTitles(firstItemTitle, secondItemTitle, firstStation, secondStation)
                mapTemplateBuilder.apply {
                    setItemList(
                        ItemList.Builder().apply {
                            addItem(
                                buildRowWithPlace(
                                    firstItemTitle, getPlaceWithMarker(
                                        firstStation.geometry.coordinates[1],
                                        firstStation.geometry.coordinates[0],
                                        if (stationToNavigateTo != null) CarColor.GREEN else CarColor.createCustom(
                                            Color.Transparent.hashCode(),
                                            Color.Transparent.hashCode()
                                        ),
                                        firstItemIcon
                                    )
                                ) {
                                    onItemClick(firstStation)
                                })
                            secondStation?.let {
                                addItem(
                                    buildRowWithPlace(
                                        secondItemTitle, getPlaceWithMarker(
                                            it.geometry.coordinates[1],
                                            it.geometry.coordinates[0],
                                            CarColor.createCustom(
                                                Color.Transparent.hashCode(),
                                                Color.Transparent.hashCode()
                                            ),
                                            carContext.getDrawable(R.drawable.electric_car_icon)
                                                ?.toBitmap()
                                        )
                                    ) {
                                        onItemClick(it)
                                    }
                                )
                            }
                        }.build()
                    )
                }

            }
        }
        mapTemplateBuilder.setTitle(mapTitle)
        return mapTemplateBuilder.build()
    }

    private fun calculateDistanceAndGetTitles(
        firstItemTitle: SpannableString,
        secondItemTitle: SpannableString,
        firstStation: Station,
        secondStation: Station?
    ) {
        val distanceResult: FloatArray = floatArrayOf(0.0f)
        Location.distanceBetween(
            initialUserLocation!!.latitude,
            initialUserLocation!!.longitude,
            firstStation.geometry.coordinates[1],
            firstStation.geometry.coordinates[0],
            distanceResult
        )
        firstItemTitle.setSpan(
            DistanceSpan.create(create(distanceResult[0] / 1000.toDouble(), UNIT_KILOMETERS_P1)),
            firstItemTitle.length,
            firstItemTitle.length,
            SPAN_INCLUSIVE_INCLUSIVE
        )
        secondStation?.let {
            Location.distanceBetween(
                initialUserLocation!!.latitude,
                initialUserLocation!!.longitude,
                it.geometry.coordinates[1],
                it.geometry.coordinates[0],
                distanceResult
            )
            secondItemTitle.setSpan(
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

