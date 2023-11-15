package com.example.emobilitychargingstations

import app.cash.sqldelight.db.SqlDriver
import com.example.emobilitychargingstations.data.local.DatabaseDriverFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module
import repositoryModule
import useCaseModule

fun initializeKoin() = startKoin {
        modules(
            repositoryModule(),
            useCaseModule(),
            module {
                single { UseCasesProvider() }
                single { provideSqlDriver() }
            }
        )
    }

fun provideSqlDriver(): SqlDriver {
    return DatabaseDriverFactory().createDriver()
}
