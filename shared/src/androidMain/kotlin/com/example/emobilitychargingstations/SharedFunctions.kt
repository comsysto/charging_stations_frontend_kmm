package com.example.emobilitychargingstations

import com.comsystoreply.emobilitychargingstations.BuildConfig
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import kotlinx.serialization.json.Json

actual class SharedFunctions {
    actual fun getStationsFromJson(): Stations? {
        val munichStations = javaClass.classLoader!!.getResourceAsStream("munichStations.json")!!.bufferedReader().use { it.readText() }
        val regensburgStationsJson = javaClass.classLoader!!.getResourceAsStream("regensburgStations.json")!!.bufferedReader().use { it.readText() }
        var stationsFromJson = Json.decodeFromString<Stations>(munichStations)
        val regensburgStationsFromJson = Json.decodeFromString<Stations>(regensburgStationsJson)
        val combinedStations = mutableListOf<Station>()
        stationsFromJson.features?.let { combinedStations.addAll(it) }
        regensburgStationsFromJson.features?.let { combinedStations.addAll(it) }
        stationsFromJson = stationsFromJson.copy(features = combinedStations.filter { it.properties.street != null })
        return stationsFromJson
    }

    actual val isDebug = BuildConfig.DEBUG
}