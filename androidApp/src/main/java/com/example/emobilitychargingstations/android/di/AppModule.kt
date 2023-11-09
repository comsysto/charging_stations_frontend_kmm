package com.example.emobilitychargingstations.android.di

import android.app.Application
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.emobilitychargingstations.database.StationEntity
import com.emobilitychargingstations.database.StationsDatabase
import com.emobilitychargingstations.database.UserInfoEntity
import com.example.emobilitychargingstations.data.local.DatabaseDriverFactory
import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.data.stations.api.StationsApi
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.data.stations.StationsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.encodeToString
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
                       return Json.decodeFromString<List<Station>>(databaseValue)
                   }
                }

                override fun encode(value: List<Station>): String {
                    return Json.encodeToString(
                        value
                    )
                }
            }
        ), UserInfoEntity.Adapter(favoriteStationsAdapter = object : ColumnAdapter<List<Station>, String> {
            override fun decode(databaseValue: String): List<Station> {
                return if (databaseValue.isEmpty()){
                    listOf()
                } else {
                    return Json.decodeFromString<List<Station>>(databaseValue)
                }
            }
            override fun encode(value: List<Station>): String {
                return Json.encodeToString(
                    value
                )
            }
        })), stationsApi)
    }
}