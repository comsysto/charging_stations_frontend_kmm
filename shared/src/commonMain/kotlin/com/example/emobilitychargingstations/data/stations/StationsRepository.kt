package com.example.emobilitychargingstations.data.stations

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import com.emobilitychargingstations.database.StationsDatabase
import com.emobilitychargingstations.database.UserInfoEntity
import com.example.emobilitychargingstations.data.stations.api.StationsApi
import com.example.emobilitychargingstations.data.users.toUserInfo
import com.example.emobilitychargingstations.models.Stations
import com.example.emobilitychargingstations.models.UserInfo
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StationsRepository(stationsDatabase: StationsDatabase, val stationsApi: StationsApi) :
    StationsRepositoryImpl {

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

    override fun getUserInfo(): UserInfo? {
        return queries.getUserInfo().executeAsOneOrNull()?.toUserInfo()
    }

    override suspend fun getUserInfoAsFlow(): Flow<UserInfo?> {
        return queries.getUserInfo().asFlow().map { value: Query<UserInfoEntity> ->
            value.executeAsOneOrNull()?.toUserInfo() }
    }

    override suspend fun setUserInfo(userInfo: UserInfo) {
        queries.insertUserInfo(userInfo.chargerType, userInfo.favoriteStations)
    }
}