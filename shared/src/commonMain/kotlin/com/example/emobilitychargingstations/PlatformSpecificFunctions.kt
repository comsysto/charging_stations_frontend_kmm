package com.example.emobilitychargingstations

import com.example.emobilitychargingstations.models.Stations

expect class PlatformSpecificFunctions() {
    fun getStationsFromJson(): Stations?

    val isDebug: Boolean
}