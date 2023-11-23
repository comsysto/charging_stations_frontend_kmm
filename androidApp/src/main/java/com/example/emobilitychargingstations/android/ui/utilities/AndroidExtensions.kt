package com.example.emobilitychargingstations.android.ui.utilities

import com.example.emobilitychargingstations.android.models.ChargerTypeToggleInfo
import com.example.emobilitychargingstations.models.ChargerTypesEnum

fun ChargerTypeToggleInfo.getNameFromString(): String = when (this.chargerType) {
    ChargerTypesEnum.AC_TYPE_1 -> {"AC Type 1"}
    ChargerTypesEnum.AC_TYPE_2 -> {"Standard AC"}
    ChargerTypesEnum.DC_EU -> {"Standard DC"}
    ChargerTypesEnum.DC_CHADEMO -> {"DC CHAdeMO"}
    ChargerTypesEnum.TESLA -> {"Tesla"}
    ChargerTypesEnum.ALL -> {"All charger types"}
}