package com.example.emobilitychargingstations.data.stations

import com.emobilitychargingstations.database.StationsDatabase
import com.example.emobilitychargingstations.data.stations.api.StationsApi
import com.example.emobilitychargingstations.data.stations.database.toStations
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.models.UserLocation

class StationsRepository(stationsDatabase: StationsDatabase, val stationsApi: StationsApi) : StationsRepositoryImpl {

    private val queries = stationsDatabase.stationsQueries

    override suspend fun insertStations(stations: Stations) {
        queries.insertStation(
            stations.type, stations.features
        )
    }

    override suspend fun getStationsLocal(): Stations? {
        return queries.getAllStations().executeAsOneOrNull()?.toStations()
    }

    override suspend fun getStationsRemote(userLocation: UserLocation): Stations? {
        TODO("Finalize this once API is finished")
//        return stationsApi.requestStationsWithLocation(userLocation)
    }
}