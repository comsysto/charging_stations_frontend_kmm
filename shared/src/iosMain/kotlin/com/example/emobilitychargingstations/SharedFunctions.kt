package com.example.emobilitychargingstations

import com.example.emobilitychargingstations.models.Stations
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import platform.Foundation.NSBundle

actual class SharedFunctions {
    actual fun getStationsFromJson(): Stations? {
        val munichStationsPath = NSBundle.mainBundle.pathForResource("munichStations", "json")
        val munichSource = FileSystem.SYSTEM.source(munichStationsPath!!.toPath()).buffer().readUtf8()
        val convertedJson = Json.decodeFromString<Stations>(munichSource)
        return convertedJson
    }
}