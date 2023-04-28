package com.example.emobilitychargingstations.data.local

import com.comsystoreply.chargingstations.database.StationsDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(StationsDatabase.Schema, "stations.db")
    }
}