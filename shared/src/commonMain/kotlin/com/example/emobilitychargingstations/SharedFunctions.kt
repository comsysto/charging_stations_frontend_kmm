package com.example.emobilitychargingstations

import com.example.emobilitychargingstations.models.Stations

expect class SharedFunctions() {
    fun getStationsFromJson(): Stations?
}