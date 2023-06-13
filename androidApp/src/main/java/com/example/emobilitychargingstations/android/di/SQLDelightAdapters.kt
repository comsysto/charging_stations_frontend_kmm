package com.example.emobilitychargingstations.android.di

import com.example.emobilitychargingstations.domain.stations.Station
import com.squareup.sqldelight.ColumnAdapter

interface SQLDelightAdapters {
    fun stationConverter(): ColumnAdapter<List<Station>, String>
}