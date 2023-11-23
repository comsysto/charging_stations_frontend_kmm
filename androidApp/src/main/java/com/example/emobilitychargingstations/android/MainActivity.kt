package com.example.emobilitychargingstations.android

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.car.app.connection.CarConnection
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comsystoreply.emobilitychargingstations.android.BuildConfig
import com.comsystoreply.emobilitychargingstations.android.MyApplicationTheme
import com.example.emobilitychargingstations.android.ui.composables.ChargerTypeSelectionComposable
import com.example.emobilitychargingstations.android.ui.composables.MapViewComposable
import com.example.emobilitychargingstations.android.ui.composables.StationsFilterComposable
import com.example.emobilitychargingstations.android.ui.utilities.LocationRequestStarter
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class MainActivity : ComponentActivity() {

    private val stationsViewModel: StationsViewModel by viewModel()
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.firstOrNull()?.let {
                if (checkIsLocationMockDebug(it))  {
                    val isInitialUserLocationNull = stationsViewModel._userLocation.value == null
                    stationsViewModel.setUserLocation(
                        GeoPoint(
                            it.latitude,
                            it.longitude
                        )
                    )
                    if (isInitialUserLocationNull) stationsViewModel.getTestStations()
                }
            }
        }
    }

    private fun checkIsLocationMockDebug(location: Location) : Boolean {
        return if (BuildConfig.DEBUG) location.isMock else true
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var startDestination = NAVIGATE_TO_CHARGER_SELECTION
        val userInfo = stationsViewModel.getUserInfo()
        Log.v("Test User", userInfo.toString())
        if (userInfo?.chargerType != null) startDestination = NAVIGATE_TO_MAP_SCREEN
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    startRepeatingRequests()
                            setContent {
                                val carConnection = CarConnection(this).type.observeAsState()
                                MyApplicationTheme {
                                    val navController = rememberNavController()
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colors.background
                                    ) {
                                        Column {
                                            NavHost(
                                                navController = navController,
                                                startDestination = startDestination
                                            ) {
                                                composable(NAVIGATE_TO_CHARGER_SELECTION) {
                                                    ChargerTypeSelectionComposable(proceedToNextScreen = {
                                                        navController.navigate(
                                                            NAVIGATE_TO_MAP_SCREEN
                                                        )
                                                    })
                                                }
                                                composable(NAVIGATE_TO_MAP_SCREEN) {
                                                    MapViewComposable(proceedToSocketSelection = {
                                                        navController.navigate(
                                                            NAVIGATE_TO_CHARGER_SELECTION
                                                        )
                                                    })
                                                }
                                                composable(NAVIGATE_TO_FILTER_SCREEN) {
                                                    StationsFilterComposable()
                                                }
                                            }
                                            if (userInfo?.chargerType != null) when (carConnection.value) {
                                                CarConnection.CONNECTION_TYPE_PROJECTION -> {
                                                    navController.navigate(NAVIGATE_TO_FILTER_SCREEN) {
                                                        popUpTo(NAVIGATE_TO_FILTER_SCREEN)
                                                    }
                                                }
                                                else -> {
                                                    if (navController.currentBackStackEntry?.destination?.route != NAVIGATE_TO_MAP_SCREEN) navController.navigate(NAVIGATE_TO_MAP_SCREEN)
                                                }
                                            }
                                        }
                                    }
                                }
                    }
                }
            }
        }.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun startRepeatingRequests() {
        LocationRequestStarter(this, locationCallback)
    }
    companion object {
        private const val NAVIGATE_TO_CHARGER_SELECTION = "chargerSelectionScreen"
        private const val NAVIGATE_TO_MAP_SCREEN = "mapScreen"
        private const val NAVIGATE_TO_FILTER_SCREEN = "filterScreen"
    }
}