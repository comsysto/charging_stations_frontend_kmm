package com.example.emobilitychargingstations.data.extensions

import com.example.emobilitychargingstations.models.ChargingTypeEnum

fun Double?.getChargingTypeFromMaxKW(): ChargingTypeEnum? {
    return if (this == null) null
        else if (this <= 6.99) ChargingTypeEnum.NORMAL
        else if (this in 7.0 .. 42.99) ChargingTypeEnum.FAST
        else ChargingTypeEnum.RAPID
}