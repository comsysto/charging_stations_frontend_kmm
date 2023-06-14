package com.example.emobilitychargingstations.data.stations

import com.comsystoreply.chargingstations.database.StationsDatabase
import com.example.emobilitychargingstations.domain.stations.Stations
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl

class StationsDataSource(db: StationsDatabase) : StationsDataSourceImpl {

    private val queries = db.stationsQueries

    override suspend fun insertStations(stations: Stations) {
        queries.insertStation(
            stations.type, stations.features
        )
    }

    override suspend fun getAllStations(): Stations? {
        return queries.getAllStations().executeAsOneOrNull()?.toStations()
    }
}