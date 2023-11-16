package com.example.emobilitychargingstations.android

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comsystoreply.emobilitychargingstations.android.BuildConfig
import com.comsystoreply.emobilitychargingstations.android.MyApplicationTheme
import com.example.emobilitychargingstations.android.ui.composables.ChargerTypeSelectionScreen
import com.example.emobilitychargingstations.android.ui.composables.ComposableMapView
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
                if (checkIsLocationMockDebug(it)) stationsViewModel.setUserLocation(
                    GeoPoint(
                        it.latitude,
                        it.longitude
                    )
                )
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
        if (userInfo?.chargerType != null) startDestination = NAVIGATE_TO_MAP_SCREEN
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    startRepeatingRequests()
                            setContent {
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
                                                    ChargerTypeSelectionScreen(proceedToNextScreen = {
                                                        navController.navigate(
                                                            NAVIGATE_TO_MAP_SCREEN
                                                        )
                                                    }, viewModel = stationsViewModel)
                                                }
                                                composable(NAVIGATE_TO_MAP_SCREEN) {
                                                    ComposableMapView(proceedToSocketSelection = {
                                                        navController.navigate(
                                                            NAVIGATE_TO_CHARGER_SELECTION
                                                        )
                                                    }, stationsViewModel = stationsViewModel)
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
        stationsViewModel.getTestStations()
    }
    companion object {
        private const val NAVIGATE_TO_CHARGER_SELECTION = "chargerSelectionScreen"
        private const val NAVIGATE_TO_MAP_SCREEN = "mapScreen"
    }
}