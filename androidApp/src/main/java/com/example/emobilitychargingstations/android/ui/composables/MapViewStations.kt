package com.example.emobilitychargingstations.android.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.StationsViewModel
import com.example.emobilitychargingstations.domain.stations.Stations
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.utils.BonusPackHelper
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun mapView(stationsViewModel: StationsViewModel) {
    val testStations = stationsViewModel._stationsData.observeAsState()
    stationsViewModel.getTestStations(LocalContext.current)
    val mapViewState = mapViewWithLifecycle(testStations.value)
    AndroidView({ mapViewState }, Modifier.fillMaxSize()) {}
}

//LEIPZIG COORDINATES
private val userLocation = GeoPoint(51.3397, 12.3731)

@Composable
fun mapViewWithLifecycle(stations: Stations?): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.layout_map
            clipToOutline = true
        }
    }
    mapView.minZoomLevel = 4.00
    mapView.maxZoomLevel = 17.00
    mapView.isHorizontalMapRepetitionEnabled = false
    mapView.isVerticalMapRepetitionEnabled = false
    if (stations != null) {
        val folderOverlay = FolderOverlay()
        val markerCluster = RadiusMarkerClusterer(context)
        markerCluster.setIcon(BonusPackHelper.getBitmapFromVectorDrawable(context, org.osmdroid.bonuspack.R.drawable.marker_cluster))
        mapView.overlays.add(folderOverlay)
        stations.features?.forEach {
            if (userLocation.latitude in it.geometry.coordinates[1] -1 .. it.geometry.coordinates[1] + 1 && userLocation.longitude in it.geometry.coordinates[0] -1 .. it.geometry.coordinates[0] + 1) {
                val stationGeoPoint = GeoPoint(it.geometry.coordinates[1], it.geometry.coordinates[0])
                val stationMarker = Marker(mapView)
                stationMarker.position = stationGeoPoint
                stationMarker.snippet = it.properties.operator
                stationMarker.icon = context.getDrawable(R.drawable.electric_car_icon)
                stationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                markerCluster.add(stationMarker)
            }
        }
        if (markerCluster.items.size > 0) folderOverlay.add(markerCluster)
        mapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(listOf(userLocation)), false)
    }
    val lifecycleObserver = rememberMapObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }
    return mapView
}

@Composable
fun rememberMapObserver(mapView: MapView): LifecycleEventObserver = remember(mapView) {
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            else -> {}
        }
    }
}