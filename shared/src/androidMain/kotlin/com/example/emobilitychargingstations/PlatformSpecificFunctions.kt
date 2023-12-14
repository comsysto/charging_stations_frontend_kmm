package com.example.emobilitychargingstations

import com.comsystoreply.emobilitychargingstations.BuildConfig
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import kotlinx.serialization.json.Json

actual class PlatformSpecificFunctions {
    actual fun getStationsFromJson(): Stations? {
        val munichStationsJson = javaClass.classLoader!!.getResourceAsStream("munichStations.json")!!.bufferedReader().use { it.readText() }
        val regensburgStationsJson = javaClass.classLoader!!.getResourceAsStream("regensburgStations.json")!!.bufferedReader().use { it.readText() }
        val munichStationsFromJson = Json.decodeFromString<Stations>(munichStationsJson)
        val regensburgStationsFromJson = Json.decodeFromString<Stations>(regensburgStationsJson)
        val combinedStations = mutableListOf<Station>()
        munichStationsFromJson.features?.let { combinedStations.addAll(it) }
        regensburgStationsFromJson.features?.let { combinedStations.addAll(it) }
        return Stations(type = "", features = combinedStations.filter { it.properties.street != null })
    }

    actual val isDebug = BuildConfig.DEBUG
}