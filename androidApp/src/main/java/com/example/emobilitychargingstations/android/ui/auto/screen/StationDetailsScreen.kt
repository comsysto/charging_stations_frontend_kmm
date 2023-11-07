package com.example.emobilitychargingstations.android.ui.auto.screen

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.extensions.buildRowWithText
import com.example.emobilitychargingstations.android.ui.auto.extensions.getFavoritesAction
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString
import com.example.emobilitychargingstations.android.ui.utilities.AUTO_POI_MAP_SCREEN_MARKER
import com.example.emobilitychargingstations.data.extensions.getChargingTypeFromMaxKW
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.UserInfo
import kotlinx.coroutines.launch

class StationDetailsScreen(carContext: CarContext, val station: Station, val stationsRepository: StationsRepositoryImpl? = null) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val stationsPane = Pane.Builder().apply {
            val userInfo = stationsRepository?.getUserInfo()
            val actionTitle = if (station.isNavigatingTo) getString(R.string.auto_station_details_stop_navigation) else getString(R.string.auto_station_details_start_navigation)
            addAction(
                Action.Builder().apply{
                    setTitle(actionTitle)
                    setBackgroundColor(CarColor.GREEN)
                    setOnClickListener(this@StationDetailsScreen::changeNavigation).build()
                }.build()
            )
            if (stationsRepository != null) addAction(getFavoritesAction(station, userInfo, ::onFavoriteChanged))
            addRow(
                buildRowWithText(
                    title = SpannableString(getString(R.string.auto_station_details_operator)),
                    text = station.properties.operator ?: "-"
                )
            )
            addRow(
                buildRowWithText(
                    title = SpannableString(getString(R.string.auto_station_details_station_capacity)),
                    text = station.properties.capacity.toString()
                )
            )
            addRow(buildRowWithText(SpannableString(getString(R.string.auto_station_details_station_charger_type_list)), getSocketTypeString()))

        }.build()

        return PaneTemplate.Builder(stationsPane).setTitle(getPaneTitle())
            .setHeaderAction(Action.BACK)
            .build()
    }

    private fun getPaneTitle(): String {
        return "${station.properties.street}" + " - ${station.properties.max_kw.getChargingTypeFromMaxKW()}"
    }

    private fun getSocketTypeString() : String {
        val socketTypeString = if (station.properties.socket_type_list == null) getString(R.string.auto_station_details_unknown_charger) else {
            var resultingString = ""
            station.properties.socket_type_list!!.groupingBy { it }.eachCount().filterValues { it >= 1 }.keys.forEach {
                resultingString = if (resultingString.isEmpty()) it
                else "$resultingString, $it"
            }
            resultingString
        }
        return socketTypeString
    }

    private fun changeNavigation() {
        if (station.isNavigatingTo)  {
            station.isNavigatingTo = false
            setResult(station)
        }
        else {
            station.isNavigatingTo = true
            setResult(station)
            val latitude = station.geometry.coordinates[1]
            val longitude = station.geometry.coordinates[0]
            val name = getString(R.string.auto_station_details_navigating_to, station.properties.street ?: "")
            val intent = Intent(CarContext.ACTION_NAVIGATE, Uri.parse("geo:0,0?q=${latitude},${longitude}(${name})"))
            carContext.startCarApp(intent)
        }
        screenManager.popTo(AUTO_POI_MAP_SCREEN_MARKER)
    }

    private fun onFavoriteChanged(userInfo: UserInfo) {
        lifecycleScope.launch {
            stationsRepository?.setUserInfo(userInfo)
            screenManager.popTo(AUTO_POI_MAP_SCREEN_MARKER)
        }
    }

}