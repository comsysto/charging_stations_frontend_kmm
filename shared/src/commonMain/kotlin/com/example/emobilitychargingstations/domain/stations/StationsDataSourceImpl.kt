package com.example.emobilitychargingstations.domain.stations

interface StationsDataSourceImpl {
    suspend fun insertStation(station: Station)
    suspend fun getAllStations(): List<Station>
    suspend fun insertTestStation()
}