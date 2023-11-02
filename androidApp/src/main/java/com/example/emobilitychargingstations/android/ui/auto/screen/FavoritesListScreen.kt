package com.example.emobilitychargingstations.android.ui.auto.screen

import android.text.SpannableString
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.OnScreenResultListener
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.Item
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.toBitmap
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.extensions.BuildRowWithTextAndIcon
import com.example.emobilitychargingstations.android.ui.auto.extensions.getMessageTemplateBuilderWithTitle
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString
import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.models.Station

class FavoritesListScreen(carContext: CarContext, val stationsRepository: StationsRepositoryImpl, val onScreenResultListener: OnScreenResultListener? = null): Screen(carContext) {

    val constraintManager = carContext.getCarService(ConstraintManager::class.java)
    val listItemLimit = constraintManager.getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_LIST)
    override fun onGetTemplate(): Template {
        Log.v("TEST LIST ITEM LIMIT", listItemLimit.toString())
        val userInfo = stationsRepository.getUserInfo()
        val templateTitle = getString(R.string.auto_favorites_list_title)
        var templateForDisplay: Template?
        if (userInfo?.favoriteStations == null || userInfo.favoriteStations.isNullOrEmpty()) templateForDisplay = getMessageTemplateBuilderWithTitle(templateTitle, getString(R.string.auto_favorites_list_empty_message)).build()
        else {
            val listTemplateBuilder = ListTemplate.Builder()
            listTemplateBuilder.setHeaderAction(Action.BACK)
            listTemplateBuilder.setTitle(templateTitle)
            listTemplateBuilder.setSingleList(ItemList.Builder().apply {
                userInfo.favoriteStations!!.forEach {
                    addItem(
                        BuildRowWithTextAndIcon(
                            SpannableString(it.properties.street),
                            it.properties.operator ?: "",
                            carContext.getDrawable(
                                R.drawable.electric_car_icon
                            )!!.toBitmap()
                        ) {
                            onItemClick(it)
                        }
                    )
                }
            }.build())
            templateForDisplay = listTemplateBuilder.build()
        }
        return templateForDisplay
    }

    private fun onItemClick(station: Station) {
        if (onScreenResultListener != null) screenManager.pushForResult(StationDetailsScreen(carContext, station, stationsRepository), onScreenResultListener)
        else screenManager.push(StationDetailsScreen(carContext, station, stationsRepository))
    }
}

