package com.example.emobilitychargingstations.android.ui.auto.screen

import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.OnScreenResultListener
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.toBitmap
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.BaseScreen
import com.example.emobilitychargingstations.android.ui.utilities.BuildRowWithTextAndIcon
import com.example.emobilitychargingstations.android.ui.utilities.getMessageTemplateBuilderWithTitle
import com.example.emobilitychargingstations.android.ui.utilities.getString
import com.example.emobilitychargingstations.models.Station

class FavoritesListScreen(carContext: CarContext, val onScreenResultListener: OnScreenResultListener? = null): BaseScreen(carContext) {

    override fun onGetTemplate(): Template {
        val userInfo = userUseCase.getUserInfo()
        val templateTitle = getString(R.string.auto_favorites_list_title)
        var templateForDisplay: Template?
        if (userInfo?.favoriteStations == null || userInfo.favoriteStations.isNullOrEmpty()) templateForDisplay = getMessageTemplateBuilderWithTitle(templateTitle, getString(R.string.auto_favorites_list_empty_message)).build()
        else {
            val listTemplateBuilder = ListTemplate.Builder()
            listTemplateBuilder.apply {
                setHeaderAction(Action.BACK)
                setTitle(templateTitle)
                setSingleList(ItemList.Builder().apply {
                    userInfo.favoriteStations!!.forEach {
                        addItem(
                            BuildRowWithTextAndIcon(
                                SpannableString(it.properties.street),
                                it.properties.operator ?: "",
                                carContext.getDrawable(
                                    R.drawable.electric_car_icon_white
                                )!!.toBitmap()
                            ) {
                                onItemClick(it)
                            }
                        )
                    }
                }.build())
            }
            templateForDisplay = listTemplateBuilder.build()
        }
        return templateForDisplay
    }

    private fun onItemClick(station: Station) {
        if (onScreenResultListener != null) screenManager.pushForResult(StationDetailsScreen(carContext, station, true), onScreenResultListener)
        else screenManager.push(StationDetailsScreen(carContext, station, true))
    }
}

