package com.example.emobilitychargingstations.android.models

import androidx.car.app.model.Item
import com.example.emobilitychargingstations.domain.stations.StationGeoData
import com.example.emobilitychargingstations.domain.stations.StationProperties

data class StationItem (
    val id: Long,
    val type: String?,
    val properties: StationProperties,
    val geometry: StationGeoData
    ): Item