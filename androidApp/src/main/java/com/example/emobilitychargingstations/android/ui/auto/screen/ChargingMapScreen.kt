package com.example.emobilitychargingstations.android.ui.auto.screen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.text.SpannableString
import android.text.Spanned
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import androidx.car.app.CarContext
import androidx.car.app.OnScreenResultListener
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
import com.example.emobilitychargingstations.android.ui.auto.extensions.createCarIconFromBitmap
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString
import com.example.emobilitychargingstations.android.ui.utilities.LOCATION_REQUEST_DISTANCE_DIFFERENCE_IN_METERS
import com.example.emobilitychargingstations.android.ui.utilities.LOCATION_REQUEST_REFRESH_VALUE_IN_MS
import com.example.emobilitychargingstations.android.ui.utilities.NAVIGATION_DISTANCE_VALUE_FOR_COMPLETION_IN_METERS
import com.example.emobilitychargingstations.data.extensions.getStationsClosestToUserLocation
import com.example.emobilitychargingstations.data.extensions.getTwoStationsClosestToUser
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.StationGeoData
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class ChargingMapScreen(carContext: CarContext, val stationsList: Stations, val stationsRepo: StationsRepositoryImpl) : Screen(carContext), LocationListener, OnScreenResultListener {

    private var userInfo = stationsRepo.getUserInfo()
    private val locationManager: LocationManager = carContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var initialUserLocation: UserLocation? = null
    private var closestStations: List<Station> = listOf()
    private var stationToNavigateTo: Station? = null
    private val locationRequest =
        LocationRequest.Builder(LOCATION_REQUEST_REFRESH_VALUE_IN_MS).apply {
            setMinUpdateDistanceMeters(LOCATION_REQUEST_DISTANCE_DIFFERENCE_IN_METERS)
        }.build()

    override fun onLocationChanged(location: Location) {
        location?.let {
            if (checkIsLocationMockDebug(it)) {
                if (stationToNavigateTo != null && getDistanceValue(it, stationToNavigateTo!!.geometry) < NAVIGATION_DISTANCE_VALUE_FOR_COMPLETION_IN_METERS) pushDestinationReachedScreen(stationToNavigateTo!!)
                else {
                    initialUserLocation = UserLocation(it.latitude, it.longitude)
                    filterStations()
                    invalidate()
                 }
            }
        }
    }

    private fun pushDestinationReachedScreen(station: Station) {
        closestStations[0].isNavigatingTo = false
        filterStations()
        stationToNavigateTo = null
        screenManager.push(NavigationCompleteScreen(carContext, station, stationsRepo))
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
        if (result != null) {
            val station = result as Station
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
        locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, locationRequest, carContext.mainExecutor, this)
    }

    private fun filterStations() {
        closestStations = if (stationsList.features.isNullOrEmpty() || initialUserLocation == null) listOf()
        else stationsList.getStationsClosestToUserLocation(initialUserLocation!!.latitude, initialUserLocation!!.longitude).getTwoStationsClosestToUser(initialUserLocation!!.latitude, initialUserLocation!!.longitude, userInfo?.chargerType)
    }

    private fun checkIsLocationMockDebug(location: Location) : Boolean {
        return if (BuildConfig.DEBUG) location.isMock else true
    }
    init {
        locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, locationRequest, carContext.mainExecutor, this)
        locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)?.let {
            it?.let {
                if (it.isMock || initialUserLocation == null) {
                    initialUserLocation = UserLocation(it.latitude, it.longitude)
                    filterStations()
                    invalidate()
                }
            }
        }
        this.marker = "MAIN SCREEN"
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
        val openFavoritesListAction = Action.Builder().apply {
            setIcon(createCarIconFromBitmap(
                carContext.getDrawable(R.drawable.favorites_star_icon)!!.toBitmap())
            )
            setOnClickListener(ParkedOnlyOnClickListener.create {
            screenManager.push(FavoritesListScreen(carContext, stationsRepo, this@ChargingMapScreen))
        })
        }.build()
        var mapTitle = getString(R.string.auto_map_title)
        val actionStrip = ActionStrip.Builder().addAction(openFavoritesListAction).build()
        val mapTemplateBuilder = PlaceListMapTemplate.Builder().setActionStrip(actionStrip)
        if (initialUserLocation == null) mapTemplateBuilder.setLoading(true)
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
//                    closestStations.forEach {
//                        if (it.id != stationToNavigateTo!!.id) {
//                            secondStation = it
//                            return@forEach
//                        }
//                    }
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
            locationManager.removeUpdates(this)
            screenManager.pushForResult(StationDetailsScreen(carContext, station = station), this)
    }

}

