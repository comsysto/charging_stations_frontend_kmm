package com.example.emobilitychargingstations.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import com.comsystoreply.emobilitychargingstations.android.BuildConfig
import com.comsystoreply.emobilitychargingstations.android.MyApplicationTheme
import com.example.emobilitychargingstations.android.ui.composables.ComposableMapView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val stationsViewModel: StationsViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions -> when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    fusedLocationClient.lastLocation.addOnCompleteListener {
                        stationsViewModel.setUserLocation(GeoPoint(it.result.latitude, it.result.longitude))
                        setContent {
                            MyApplicationTheme {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colors.background
                                ) {
                                    ComposableMapView(stationsViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        super.onCreate(savedInstanceState)
    }
}