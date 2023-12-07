package com.example.emobilitychargingstations.android

import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.car.app.connection.CarConnection
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.comsystoreply.emobilitychargingstations.android.BuildConfig
import com.comsystoreply.emobilitychargingstations.android.MyApplicationTheme
import com.example.emobilitychargingstations.android.ui.composables.FilteringOptionsComposable
import com.example.emobilitychargingstations.android.ui.composables.MapViewComposable
import com.example.emobilitychargingstations.android.ui.composables.StationsFilterComposable
import com.example.emobilitychargingstations.android.ui.utilities.LocationRequestStarter
import com.example.emobilitychargingstations.models.ChargerTypesEnum
import com.example.emobilitychargingstations.models.UserLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    private val stationsViewModel: StationsViewModel by viewModel()
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.firstOrNull()?.let {
                if (checkIsLocationMockDebug(it))  {
                    val isInitialUserLocationNull = stationsViewModel.userLocation.value == null
                    stationsViewModel.setUserLocation(
                        UserLocation(
                            it.latitude,
                            it.longitude
                        )
                    )
                    if (isInitialUserLocationNull) stationsViewModel.startRepeatingStationsRequest()
                }
            }
        }
    }

    private fun checkIsLocationMockDebug(location: Location) : Boolean {
        return if (BuildConfig.DEBUG) location.isMock else true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userInfo = stationsViewModel.getUserInfo()
        val startDestination =
            if (userInfo?.filterProperties?.chargerType != null) NAVIGATE_TO_MAP_SCREEN else NAVIGATE_TO_CHARGER_SELECTION
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
                                    NavigationHostComposable(navController, startDestination)
                                    ObserveCarConnectionComposable(
                                        navController,
                                        userInfo?.filterProperties?.chargerType
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    @Composable
    private fun ObserveCarConnectionComposable(navController: NavHostController, chargerType: ChargerTypesEnum?) {
        val carConnection = CarConnection(this).type.observeAsState()
        if (chargerType != null) when (carConnection.value) {
            CarConnection.CONNECTION_TYPE_PROJECTION -> {
                stationsViewModel.stopRepeatingStationsRequest()
                navController.currentDestination?.route?.let {
                    navController.popBackStack(
                        it, true)
                }
                navController.navigate(NAVIGATE_TO_FILTER_SCREEN)
            }
            else -> {
                if (navController.currentBackStackEntry?.destination?.route != NAVIGATE_TO_MAP_SCREEN) {
                    navController.navigate(NAVIGATE_TO_MAP_SCREEN)
                    stationsViewModel.startRepeatingStationsRequest()
                }
            }
        }
    }

    @Composable
    private fun NavigationHostComposable(navController: NavHostController, startDestination: String) = NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("$NAVIGATE_TO_CHARGER_SELECTION?$ARGUMENT_NAVIGATE_TO_NEXT={$ARGUMENT_NAVIGATE_TO_NEXT}", arguments = listOf(
            navArgument(ARGUMENT_NAVIGATE_TO_NEXT) {
                type = NavType.BoolType
                defaultValue = true
            }
        )) { backStackEntry ->
            val shouldNavigateToMap = backStackEntry.arguments?.getBoolean(ARGUMENT_NAVIGATE_TO_NEXT)
            FilteringOptionsComposable(proceedToNextScreen = {
                if (shouldNavigateToMap == true) navController.navigate(
                    NAVIGATE_TO_MAP_SCREEN
                ) else navController.popBackStack()
            })
        }
        composable(NAVIGATE_TO_MAP_SCREEN) {
            MapViewComposable(proceedToSocketSelection = {
                navController.navigate(
                    NAVIGATE_TO_FILTER_SCREEN
                )
            })
        }
        composable(NAVIGATE_TO_FILTER_SCREEN) {
            StationsFilterComposable(navigateToChargerType = {
                navController.navigate(
                    "$NAVIGATE_TO_CHARGER_SELECTION?$ARGUMENT_NAVIGATE_TO_NEXT=false")
            })
        }
    }

    private fun startRepeatingRequests() {
        LocationRequestStarter(this, locationCallback)
    }
    companion object {
        private const val NAVIGATE_TO_CHARGER_SELECTION = "chargerSelectionScreen"
        private const val NAVIGATE_TO_MAP_SCREEN = "mapScreen"
        private const val NAVIGATE_TO_FILTER_SCREEN = "filterScreen"

        private const val ARGUMENT_NAVIGATE_TO_NEXT = "navigateToNext"
    }
}