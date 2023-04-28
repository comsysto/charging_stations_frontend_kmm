package com.example.emobilitychargingstations.data.stations

import com.example.emobilitychargingstations.domain.stations.Station
import database.StationEntity

fun StationEntity.toStations(): Station {
    return Station(
        id = id,
        address = address,
        ltd = ltd,
        lng = lng,
        operator = operator_,
        capacity = capacity
    )
}