package com.example.emobilitychargingstations.data.stations

import com.emobilitychargingstations.database.StationEntity
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.StationGeoData
import com.example.emobilitychargingstations.models.StationProperties
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.StationsResponseModel

fun StationEntity.toStations(): Stations {
    return Stations(
        type = this.type,
        features = this.features
    )
}

fun List<StationsResponseModel>.toStationList(): List<Station> {
    val resultingList = mutableListOf<Station>()
    this.forEach {
        val station = Station(
            id = it.id,
            type = null,
            properties = StationProperties(
                capacity = it.totalChargingStations.toDouble(),
                data_source = it.dataSource,
                dc_support = null,
                max_kw = null,
                operator = it.operator,
//                socket_type_list = it.connections,
                socket_type_list = null,
                station_id = it.stationId,
                street = it.street,
                total_kw = null,
                town = it.town,
                availableChargingStations = it.availableChargingStations
            ),
            geometry = StationGeoData(
                type = "POINT",
                coordinates = arrayOf(it.longitude, it.latitude)
            )
        )
        resultingList.add(station)
    }
    return resultingList
}