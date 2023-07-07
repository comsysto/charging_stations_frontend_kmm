package com.example.emobilitychargingstations.domain.stations

interface StationsDataSourceImpl {
    suspend fun insertStations(stations: Stations)
    suspend fun getAllStations(): Stations?
}