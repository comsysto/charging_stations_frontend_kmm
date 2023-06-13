package com.example.emobilitychargingstations.domain.stations

interface StationsDataSourceImpl {
    suspend fun insertStation(stations: Stations)
    suspend fun getAllStations(): Stations?
    suspend fun insertTestStation()
}