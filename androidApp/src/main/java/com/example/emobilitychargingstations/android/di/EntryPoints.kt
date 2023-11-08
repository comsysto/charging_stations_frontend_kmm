package com.example.emobilitychargingstations.android.di

import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class EntryPoints {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ScreenEntryPoint {
        fun stationsRepo(): StationsRepositoryImpl
    }
}