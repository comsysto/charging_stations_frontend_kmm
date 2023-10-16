package com.example.emobilitychargingstations.android.di

import app.cash.sqldelight.ColumnAdapter
import com.example.emobilitychargingstations.models.Station

interface SQLDelightAdapters {
    fun stationConverter(): ColumnAdapter<List<Station>, String>
}