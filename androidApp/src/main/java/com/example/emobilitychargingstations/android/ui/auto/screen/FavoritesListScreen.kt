package com.example.emobilitychargingstations.android.ui.auto.screen

import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.OnScreenResultListener
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Item
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.toBitmap
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.extensions.BuildRowWithTextAndIcon
import com.example.emobilitychargingstations.android.ui.auto.extensions.getMessageTemplateBuilderWithTitle
import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.models.Station

class FavoritesListScreen(carContext: CarContext, val stationsRepository: StationsRepositoryImpl, val onScreenResultListener: OnScreenResultListener? = null): Screen(carContext) {
    override fun onGetTemplate(): Template {
        val userInfo = stationsRepository.getUserInfo()
        var templateForDisplay: Template?
        if (userInfo?.favoriteStations == null) templateForDisplay = getMessageTemplateBuilderWithTitle("Favorites List", "There are currently no favorites saved").build()
        else {
            val listTemplateBuilder = ListTemplate.Builder()
            listTemplateBuilder.setHeaderAction(Action.BACK)
            listTemplateBuilder.setTitle("Favorites list")
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
        if (onScreenResultListener != null) screenManager.pushForResult(StationDetailsScreen(carContext, station), onScreenResultListener)
        else screenManager.push(StationDetailsScreen(carContext, station))
    }
}

