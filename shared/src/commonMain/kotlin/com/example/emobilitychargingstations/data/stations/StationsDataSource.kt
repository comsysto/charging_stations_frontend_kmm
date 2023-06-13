package com.example.emobilitychargingstations.data.stations

import com.comsystoreply.chargingstations.database.StationsDatabase
import com.example.emobilitychargingstations.domain.stations.Station
import com.example.emobilitychargingstations.domain.stations.Stations
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import okio.FileSystem

class StationsDataSource(db: StationsDatabase) : StationsDataSourceImpl {

    private val queries = db.stationsQueries

    override suspend fun insertStation(stations: Stations) {
        queries.insertStation(
            stations.type, stations.features
        )
//        queries.insertStation(
//            station.id ?: -1,
//            station.address,
//            station.ltd,
//            station.lng,
//            station.operator ?: "",
//            station.capacity
//        )
    }

    override suspend fun getAllStations(): Stations? {
        return queries.getAllStations().executeAsOneOrNull()?.toStations()
    }

    override suspend fun insertTestStation() {
//        queries.insertStation(
//            id = 1,
//            address = "Gerhardstr. 7, München ",
//            ltd = 48.116095,
//            lng = 11.568933,
//            operator_ = "SWM",
//            capacity = "22"
//        )
    }
}