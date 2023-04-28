package com.example.emobilitychargingstations.data.local

import android.content.Context
import com.comsystoreply.chargingstations.database.StationsDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val  context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(StationsDatabase.Schema, context, "stations.db")
    }
}