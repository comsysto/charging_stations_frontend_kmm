package com.example.emobilitychargingstations.android.ui.auto.screen

import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.OnScreenResultListener
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Template
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.BaseScreen
import com.example.emobilitychargingstations.android.ui.utilities.buildClickableRowWithTextAndIcon
import com.example.emobilitychargingstations.android.ui.utilities.getDrawableAsBitmap
import com.example.emobilitychargingstations.android.ui.utilities.getMessageTemplateBuilderWithTitle
import com.example.emobilitychargingstations.android.ui.utilities.getString
import com.example.emobilitychargingstations.models.Station

class FavoritesListScreen(carContext: CarContext, private val onScreenResultListener: OnScreenResultListener? = null): BaseScreen(carContext) {

    override fun onGetTemplate(): Template {
        val userInfo = userUseCase.getUserInfo()
        val templateTitle = getString(R.string.auto_favorites_list_title)
        val templateForDisplay: Template =
            if (userInfo?.favoriteStations == null || userInfo.favoriteStations.isNullOrEmpty())
                getMessageTemplateBuilderWithTitle(templateTitle, getString(R.string.auto_favorites_list_empty_message)).build()
            else {
                val listTemplateBuilder = ListTemplate.Builder()
                listTemplateBuilder.apply {
                    setHeaderAction(Action.BACK)
                    setTitle(templateTitle)
                    setSingleList(ItemList.Builder().apply {
                        userInfo.favoriteStations!!.forEach {favorite ->
                            addItem(
                                buildClickableRowWithTextAndIcon(
                                    title = SpannableString(favorite.properties.street),
                                    text = favorite.properties.operator ?: "",
                                    carIcon = getDrawableAsBitmap(
                                        R.drawable.electric_car_icon_white
                                    )!!
                                ) {
                                    onItemClick(favorite)
                                }
                            )
                        }
                    }.build())
            }
            listTemplateBuilder.build()
        }
        return templateForDisplay
    }

    private fun onItemClick(station: Station) {
        if (onScreenResultListener != null) screenManager.pushForResult(StationDetailsScreen(carContext, station, true), onScreenResultListener)
        else screenManager.push(StationDetailsScreen(carContext, station, true))
    }
}

