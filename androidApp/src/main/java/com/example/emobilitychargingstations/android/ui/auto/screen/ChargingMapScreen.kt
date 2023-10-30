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
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString
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
        LocationRequest.Builder(4000).apply {
            setMinUpdateDistanceMeters(100f)
        }.build()

    override fun onLocationChanged(location: Location) {
        location?.let {
            if (checkIsLocationMockDebug(it)) {
                if (stationToNavigateTo != null && getDistanceValue(it, stationToNavigateTo!!.geometry) < 600) pushDestinationReachedScreen(stationToNavigateTo!!)
                else {
                    initialUserLocation = UserLocation(it.latitude, it.longitude)
                    filterStations()
                    invalidate()
                 }
            }
        }
    }

    private fun pushDestinationReachedScreen(station: Station) {
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
            stationToNavigateTo = if (station.isNavigatingTo) {
                closestStations = listOf(station, closestStations[1])
                station
            }
            else null
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

        val openFavoritesAction = Action.Builder().setIcon(CarIcon.APP_ICON).setOnClickListener {
            screenManager.push(FavoritesListScreen(carContext, stationsRepo, this))
        }.build()
        val mapTitle = getString(R.string.auto_map_title)
        val actionStrip = ActionStrip.Builder().addAction(openFavoritesAction).build()
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
            var firstStation = closestStations[0]
            var secondStation = closestStations[1]
            var firstItemTitle = SpannableString("${firstStation.properties.street} - ")
            var secondItemTitle = SpannableString("${secondStation.properties.street} - ")
            if (firstStation.isNavigatingTo)  {
                firstItemTitle = SpannableString("Navigating to: ${firstStation.properties.street} - ")
            }
            calculateDistanceAndGetTitles(firstItemTitle, secondItemTitle, closestStations)
            mapTemplateBuilder.apply {
                setItemList(
                    ItemList.Builder().apply {
                        val firstItemIcon = if (firstStation.isNavigatingTo) carContext.getDrawable(R.drawable.navigating_to_icon)?.toBitmap()
                        else carContext.getDrawable(R.drawable.electric_car_icon)?.toBitmap()
                        addItem(
                            buildRowWithPlace(
                                firstItemTitle, getPlaceWithMarker(
                                    firstStation.geometry.coordinates[1],
                                    firstStation.geometry.coordinates[0],
                                    CarColor.createCustom(
                                        Color.Transparent.hashCode(),
                                        Color.Transparent.hashCode()
                                    ),
                                    firstItemIcon
                                )
                            ) {
                                onItemClick(firstStation)
                            })
                        addItem(
                            buildRowWithPlace(
                                secondItemTitle, getPlaceWithMarker(
                                    secondStation.geometry.coordinates[1],
                                    secondStation.geometry.coordinates[0],
                                    CarColor.createCustom(
                                        Color.Transparent.hashCode(),
                                        Color.Transparent.hashCode()
                                    ),
                                    carContext.getDrawable(R.drawable.electric_car_icon)?.toBitmap()
                                )
                            ) {
                                onItemClick(secondStation)
                            }
                        )
                    }.build()
                )
            }

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

    private fun onItemClick(station: Station) {
            locationManager.removeUpdates(this)
            screenManager.pushForResult(StationDetailsScreen(carContext, station = station), this)
//            screenManager.push(
//                StationDetailsScreen(carContext, station = station)
//            )
    }

}

