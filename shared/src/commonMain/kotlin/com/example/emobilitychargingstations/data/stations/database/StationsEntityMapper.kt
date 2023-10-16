package com.example.emobilitychargingstations.data.stations.database

import com.emobilitychargingstations.database.StationEntity
import com.example.emobilitychargingstations.models.Stations

fun StationEntity.toStations(): Stations {
    return Stations(
        type = this.type,
        features = this.features
    )
}