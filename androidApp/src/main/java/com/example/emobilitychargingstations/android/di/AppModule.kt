package com.example.emobilitychargingstations.android.di

import android.app.Application
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.emobilitychargingstations.database.StationEntity
import com.emobilitychargingstations.database.StationsDatabase
import com.example.emobilitychargingstations.data.local.DatabaseDriverFactory
import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.data.stations.api.StationsApi
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.domain.stations.StationsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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
    fun provideApi(): StationsApi {
        return StationsApi()
    }

    @Provides
    @Singleton
    fun provideDataSource(driver: SqlDriver, stationsApi: StationsApi): StationsRepositoryImpl {
        return StationsRepository(StationsDatabase(driver, StationEntity.Adapter(
            featuresAdapter = object : ColumnAdapter<List<Station>, String> {
                override fun decode(databaseValue: String): List<Station> {
                   return if (databaseValue.isEmpty()){
                       listOf()
                   } else {
                       val list = mutableListOf<Station>()
                       databaseValue.split(",").forEach {
                           list.add(Json.decodeFromString(string =  it))
                       }
                       return list
                   }
                }

                override fun encode(value: List<Station>): String = value.joinToString(separator = ",")
            }
        )), stationsApi)
    }
}