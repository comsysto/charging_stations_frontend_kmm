package com.example.emobilitychargingstations.android.di

import android.app.Application
import com.comsystoreply.chargingstations.database.StationsDatabase
import com.example.emobilitychargingstations.data.local.DatabaseDriverFactory
import com.example.emobilitychargingstations.data.stations.StationsDataSource
import com.example.emobilitychargingstations.domain.stations.Station
import com.example.emobilitychargingstations.domain.stations.StationsDataSourceImpl
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import database.StationEntity
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
    fun provideDataSource(driver: SqlDriver): StationsDataSourceImpl {
        return StationsDataSource(StationsDatabase(driver, StationEntity.Adapter(
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
        )))
    }
}