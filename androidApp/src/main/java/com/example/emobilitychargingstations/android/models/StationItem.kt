package com.example.emobilitychargingstations.android.models

import androidx.car.app.model.Item
import com.example.emobilitychargingstations.models.StationGeoData
import com.example.emobilitychargingstations.models.StationProperties

data class StationItem (
    val id: Long,
    val type: String?,
    val properties: StationProperties,
    val geometry: StationGeoData
    ): Item