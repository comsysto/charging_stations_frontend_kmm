package com.example.emobilitychargingstations.android.ui.auto.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.extensions.getFavoritesAction
import com.example.emobilitychargingstations.android.ui.auto.extensions.getMessageTemplateBuilderWithTitle
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString
import com.example.emobilitychargingstations.data.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.UserInfo
import kotlinx.coroutines.launch

class NavigationCompleteScreen(carContext: CarContext, val station: Station, val stationsRepo: StationsRepositoryImpl): Screen(carContext) {

    override fun onGetTemplate(): Template {
        val userInfo = stationsRepo.getUserInfo()
        val isAlreadyInFavorites = userInfo?.favoriteStations?.firstOrNull { it.id == station.id }?.let { true } ?: false
        val title = getString(R.string.auto_navigation_complete_title)
        val body = if (isAlreadyInFavorites) getString(R.string.auto_navigation_complete_already_in_favorite_message) else getString(R.string.auto_navigation_complete_message)
        val messageTemplateBuilder = getMessageTemplateBuilderWithTitle(title, body)
        messageTemplateBuilder.apply {
            addAction(getFavoritesAction(station, userInfo, ::onFavoriteChanged))
        }
        return messageTemplateBuilder.build()
    }

    private fun onFavoriteChanged(userInfo: UserInfo) {
        lifecycleScope.launch {
            stationsRepo.setUserInfo(userInfo)
            screenManager.pop()
        }
    }
}