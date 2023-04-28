package com.example.emobilitychargingstations.android.di

import android.app.Application
import com.comsystoreply.chargingstations.database.StationsDatabase
import com.example.emobilitychargingstations.data.local.DatabaseDriverFactory
import com.example.emobilitychargingstations.data.stations.StationsDataSource
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSqlDriver(app: Application): SqlDriver {
        return DatabaseDriverFactory(app).createDriver()
    }

    @Provides
    @Singleton
    fun provideDataSource(driver: SqlDriver): StationsDataSourceImpl {
        return StationsDataSource(StationsDatabase(driver))
    }
}