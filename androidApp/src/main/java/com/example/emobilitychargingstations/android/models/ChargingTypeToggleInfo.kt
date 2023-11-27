package com.example.emobilitychargingstations.android.models

import com.example.emobilitychargingstations.models.ChargingTypeEnum

data class ChargingTypeToggleInfo (
    val isChecked: Boolean,
    val chargingType: ChargingTypeEnum
)