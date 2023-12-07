package com.example.emobilitychargingstations.android.ui.composables

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.StationsViewModel
import com.example.emobilitychargingstations.android.ui.composables.reusables.getActivityViewModel
import com.example.emobilitychargingstations.android.ui.composables.reusables.ProgressBarComposable
import com.example.emobilitychargingstations.data.extensions.getLatitude
import com.example.emobilitychargingstations.data.extensions.getLongitude
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.UserLocation
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.utils.BonusPackHelper
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker

private var shouldZoomIn: Boolean = true
private var stationListSize = 0
@Composable
fun MapViewComposable(proceedToSocketSelection: () -> Unit, stationsViewModel: StationsViewModel = getActivityViewModel()) {
    val testStations = stationsViewModel.stationsData.observeAsState()
    val userLocation = stationsViewModel.userLocation.observeAsState()
    testStations.value?.size?.let {
        shouldZoomIn = stationListSize != it
        stationListSize = it
    }
    val mapViewState = mapViewWithLifecycle(testStations.value, userLocation.value)
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (map, button, progressBar) = createRefs()
        if (testStations.value != null && userLocation.value != null) {
            AndroidView({ mapViewState },
                Modifier
                    .fillMaxSize()
                    .constrainAs(map) {}) {}
            TextButton(modifier = Modifier.constrainAs(button) {
                top.linkTo(map.top)
                end.linkTo(map.end)
            }, onClick = { proceedToSocketSelection() }) {
                Text(stringResource(R.string.android_map_edit_filters), color = Color.Black)
            }
        } else ProgressBarComposable(modifier = Modifier.constrainAs(progressBar) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
    }
}

@Composable
private fun mapViewWithLifecycle(stations: List<Station>?, userLocation: UserLocation?): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.layout_map
            clipToOutline = true
        }
    }
    mapView.apply {
        minZoomLevel = 9.00
        maxZoomLevel = 15.00
        isHorizontalMapRepetitionEnabled = false
        isVerticalMapRepetitionEnabled = false
    }
    if (stations != null && userLocation != null) {
        val userLocationAsGeoPoint = GeoPoint(userLocation.latitude, userLocation.longitude)
        addMarkersToMap(mapView, userLocationAsGeoPoint, context, stations)
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
private fun rememberMapObserver(mapView: MapView): LifecycleEventObserver = remember(mapView) {
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> {
                stationListSize = 0
                mapView.onPause()
            }
            else -> {}
        }
    }
}
private fun addMarkersToMap(mapView: MapView, userLocation: GeoPoint, context: Context, stations: List<Station>) {
    val folderOverlay = FolderOverlay()
    val userLocationAsGeoPoint = GeoPoint(userLocation.latitude, userLocation.longitude)
    val markerCluster = RadiusMarkerClusterer(context)
    markerCluster.apply {
        setIcon(BonusPackHelper.getBitmapFromVectorDrawable(context, org.osmdroid.bonuspack.R.drawable.marker_cluster))
        items.removeAll(markerCluster.items.toSet())
    }
    stations.forEach {
        val stationGeoPoint = GeoPoint(it.geometry.getLatitude(), it.geometry.getLongitude())
        val stationMarker = Marker(mapView).apply {
            position = stationGeoPoint
            snippet = it.properties.street ?: it.properties.operator
            icon = AppCompatResources.getDrawable(context,R.drawable.electric_car_icon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        markerCluster.add(stationMarker)
    }
    if (shouldZoomIn) {
        mapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(listOf(userLocationAsGeoPoint)), false)
        shouldZoomIn = false
    }
    val userMarker = Marker(mapView)
    userMarker.position = userLocationAsGeoPoint
    folderOverlay.add(userMarker)
    if (markerCluster.items.isNotEmpty()) folderOverlay.add(markerCluster)
    mapView.overlays.apply {
        clear()
        add(folderOverlay)
    }
}