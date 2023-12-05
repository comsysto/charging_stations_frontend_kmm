package com.example.emobilitychargingstations.android.ui.auto.screen

import android.Manifest
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.model.Action
import androidx.car.app.model.Template
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.ui.auto.BaseScreen
import com.example.emobilitychargingstations.android.ui.utilities.getMessageTemplateBuilderWithTitle
import com.example.emobilitychargingstations.android.ui.utilities.getString

class PermissionScreen(carContext: CarContext): BaseScreen(carContext) {
    override fun onGetTemplate(): Template {
        val message = getString(R.string.auto_permissions_message)
        val messageTemplateBuilder = getMessageTemplateBuilderWithTitle("", message)
        messageTemplateBuilder.apply {
            addAction(Action.Builder().apply {
                setOnClickListener {
                    CarToast.makeText(carContext, getString(R.string.auto_permissions_toast_message), CarToast.LENGTH_LONG).show()
                    carContext.requestPermissions(listOf(Manifest.permission.ACCESS_FINE_LOCATION)) { grantedPermissions, _ ->
                        if (grantedPermissions.isNotEmpty()) {
                            screenManager.push(ChargingMapScreen(carContext))
                            screenManager.remove(this@PermissionScreen)
                        }
                    }
                }
                setTitle(getString(R.string.auto_permissions_action_title))
            }.build())
            setHeaderAction(Action.APP_ICON)
        }
        return messageTemplateBuilder.build()
    }
}