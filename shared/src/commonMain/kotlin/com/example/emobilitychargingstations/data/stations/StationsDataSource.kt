package com.example.emobilitychargingstations.data.stations

import com.comsystoreply.chargingstations.database.StationsDatabase
import com.example.emobilitychargingstations.domain.stations.Station
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl

class StationsDataSource(db: StationsDatabase) : StationsDataSourceImpl {

    private val queries = db.stationsQueries

    override suspend fun insertStation(station: Station) {
        queries.insertStation(
            station.id ?: -1,
            station.address,
            station.ltd,
            station.lng,
            station.operator ?: "",
            station.capacity
        )
    }

    override suspend fun getAllStations(): List<Station> {
        val listOfStations = mutableListOf<Station>()
        queries.getAllStations().executeAsList().forEach {
            listOfStations.add(it.toStations())
        }
        return listOfStations
    }

    override suspend fun insertTestStation() {
        queries.insertStation(
            id = 1,
            address = "Gerhardstr. 7, München ",
            ltd = 48.116095,
            lng = 11.568933,
            operator_ = "SWM",
            capacity = "22"
        )
    }
}