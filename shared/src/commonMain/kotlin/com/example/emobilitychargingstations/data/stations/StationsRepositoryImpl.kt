package com.example.emobilitychargingstations.data.stations

import arrow.core.Either
import com.emobilitychargingstations.database.StationsDatabase
import com.example.emobilitychargingstations.data.stations.api.StationsApi
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.StationsResponseModel
import com.example.emobilitychargingstations.models.UserLocation

class StationsRepositoryImpl(stationsDatabase: StationsDatabase, val stationsApi: StationsApi) :
    StationsRepository {

    private val queries = stationsDatabase.stationsQueries

    override suspend fun insertStations(stations: Stations) {
        queries.insertStation(
            stations.type, stations.features
        )
    }

    override suspend fun getStationsLocal(): Stations? {
        return queries.getAllStations().executeAsOneOrNull()?.toStations()
    }

    override suspend fun getStationsRemote(userLocation: UserLocation?): Either<Exception, List<StationsResponseModel>> {
        return stationsApi.requestStationsWithLocation(userLocation)
    }
}