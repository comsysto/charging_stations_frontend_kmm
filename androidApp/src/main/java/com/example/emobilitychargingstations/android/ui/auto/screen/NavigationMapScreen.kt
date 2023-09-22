package com.example.emobilitychargingstations.android.ui.auto.screen

import android.app.Presentation
import android.hardware.display.VirtualDisplay
import android.location.Location
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.Distance
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.RoutingInfo
import androidx.car.app.navigation.model.Step
import com.example.emobilitychargingstations.domain.stations.Station
import org.osmdroid.util.GeoPoint

class NavigationMapScreen(carContext: CarContext, val station: Station): Screen(carContext), SurfaceCallback {

    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var presentation: Presentation
    private val userLocation = GeoPoint(51.3397, 12.3731)
    private fun isSurfaceReady(surfaceContainer: SurfaceContainer): Boolean {
        return surfaceContainer.surface != null && surfaceContainer.dpi != 0 && surfaceContainer.height != 0 && surfaceContainer.width != 0
    }

    override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
//        if (!isSurfaceReady(surfaceContainer)) return
//        virtualDisplay =
//            carContext.getSystemService(DisplayManager::class.java).createVirtualDisplay(
//                "TEST DISPLAY NAME",
//                surfaceContainer.width,
//                surfaceContainer.height,
//                surfaceContainer.dpi,
//                surfaceContainer.surface,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
//            )
//        presentation = Presentation(carContext, virtualDisplay.display)
//        val mapView = getMapViewInfo()
//        presentation.addContentView(mapView, mapView.layoutParams)
//        presentation.show()
        super.onSurfaceAvailable(surfaceContainer)
    }

    override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
        presentation.dismiss()
        virtualDisplay.release()
        super.onSurfaceDestroyed(surfaceContainer)
    }
    override fun onGetTemplate(): Template {
        val action = Action.BACK
        val customAction = Action.Builder().setTitle("Find Nearest").build()
        val actionStrip = ActionStrip.Builder().addAction(action).addAction(customAction).build()
        val distanceResult: FloatArray = floatArrayOf(0.0f)
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            station.geometry.coordinates[1],
            station.geometry.coordinates[0],
            distanceResult
        )
        return NavigationTemplate.Builder()
            .setNavigationInfo(
                RoutingInfo.Builder()
                    .setCurrentStep(
                        Step.Builder().build(),
                        Distance.create(
                            distanceResult[0] / 1000.toDouble(),
                            Distance.UNIT_KILOMETERS_P1
                        )
                    )
                    .build()
            ).setBackgroundColor(CarColor.SECONDARY)
            .setActionStrip(actionStrip)
            .build()
    }
}