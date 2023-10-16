package com.example.emobilitychargingstations.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.emobilitychargingstations.database.StationsDatabase


actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(StationsDatabase.Schema, "stations.db")
    }
}