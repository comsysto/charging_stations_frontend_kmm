package com.example.emobilitychargingstations.android.ui.auto.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.extensions.getMessageTemplateBuilderWithTitle
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.models.Station
import kotlinx.coroutines.launch

class NavigationCompleteScreen(carContext: CarContext, val station: Station, val stationsRepo: StationsRepositoryImpl): Screen(carContext) {
    override fun onGetTemplate(): Template {
        val userInfo = stationsRepo.getUserInfo()
        val isAlreadyInFavorites = userInfo?.favoriteStations?.contains(station) ?: false
        val title = getString(R.string.auto_navigation_complete_title)
        val body = if (isAlreadyInFavorites) getString(R.string.auto_navigation_complete_already_in_favorite_message) else getString(R.string.auto_navigation_complete_message)
        val actionText = if (isAlreadyInFavorites) getString(R.string.auto_navigation_complete_remove_action) else getString(R.string.auto_navigation_complete_add_action)
        val messageTemplateBuilder = getMessageTemplateBuilderWithTitle(title, body)
        messageTemplateBuilder.apply {
            addAction(Action.Builder().apply {
                setTitle(actionText)
                setOnClickListener {
                    lifecycleScope.launch {
                        if (isAlreadyInFavorites) {
                            userInfo?.favoriteStations?.remove(station)
                            stationsRepo.setUserInfo(userInfo!!)
                        } else {
                            if (userInfo?.favoriteStations.isNullOrEmpty()) stationsRepo.setUserInfo(userInfo!!.copy(favoriteStations = mutableListOf(station)))
                            else {
                                userInfo?.favoriteStations?.add(station)
                                stationsRepo.setUserInfo(userInfo!!)
                            }
                        }
                        screenManager.pop()
                    }
                }
            }.build())
        }
        return messageTemplateBuilder.build()
    }
}