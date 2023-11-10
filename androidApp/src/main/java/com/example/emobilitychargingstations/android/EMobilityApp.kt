package com.example.emobilitychargingstations.android

import android.app.Application
import com.example.emobilitychargingstations.android.di.androidKoinModule
import com.example.emobilitychargingstations.android.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import repositoryModule
import useCaseModule
class EMobilityApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EMobilityApp)
            modules (
                androidKoinModule(),
                viewModelModule(),
                repositoryModule(),
                useCaseModule()
            )
        }
    }
}