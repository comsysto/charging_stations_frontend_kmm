package com.example.emobilitychargingstations.android.ui.auto.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.extensions.getString

class NavigationCompleteScreen(carContext: CarContext): Screen(carContext) {
    override fun onGetTemplate(): Template {
        val messageTemplateBuilder = MessageTemplate.Builder(getString(R.string.auto_navigation_complete_message))
        messageTemplateBuilder.apply {
            setTitle(getString(R.string.auto_navigation_complete_title))
            setHeaderAction(Action.BACK)
            addAction(Action.Builder().apply {
                setTitle(getString(R.string.auto_navigation_complete_action))
                setOnClickListener { }
            }.build())
        }
        return messageTemplateBuilder.build()
    }
}