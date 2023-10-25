package com.example.emobilitychargingstations.data.extensions

import com.example.emobilitychargingstations.models.ChargingTypeEnum

fun Double?.getChargingTypeFromMaxKW(): String {
    var chargingType = ""
    chargingType =
        if (this == null) "Unknown"
        else if (this in 0.0 .. 6.99) ChargingTypeEnum.NORMAL.name
        else if (this in 7.0 .. 42.99) ChargingTypeEnum.FAST.name
        else ChargingTypeEnum.RAPID.name
    return chargingType.lowercase() + " charging"
}