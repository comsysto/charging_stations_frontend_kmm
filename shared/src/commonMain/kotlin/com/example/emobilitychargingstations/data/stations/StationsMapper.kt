package com.example.emobilitychargingstations.data.stations

import com.example.emobilitychargingstations.domain.stations.Stations
import database.StationEntity

fun StationEntity.toStations(): Stations {
    return Stations(
        type = this.type,
        features = this.features
    )
}