package com.example.emobilitychargingstations.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.emobilitychargingstations.database.StationsDatabase

actual class DatabaseDriverFactory(private val  context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(StationsDatabase.Schema, context, "stations.db")
    }
}