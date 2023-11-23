package com.example.emobilitychargingstations.models

import kotlinx.serialization.Serializable

@Serializable
data class StationFilterProperties(
    val chargerType: ChargerTypesEnum = ChargerTypesEnum.ALL,
    val chargingType: ChargingTypeEnum = ChargingTypeEnum.ANY
)
