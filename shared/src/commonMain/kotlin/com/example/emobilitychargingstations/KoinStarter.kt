package com.example.emobilitychargingstations

import org.koin.core.context.startKoin
import repositoryModule
import useCaseModule

fun initializeKoin() {
    startKoin {
        modules(
            repositoryModule(),
            useCaseModule()
        )
    }
}