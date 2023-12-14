package com.example.emobilitychargingstations

import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.Stations
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.serialization.json.Json
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile
import platform.darwin.NSObject
import platform.darwin.NSObjectMeta
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
actual class PlatformSpecificFunctions {

    actual fun getStationsFromJson(): Stations? {
        val bundle = NSBundle.bundleForClass(BundleMarker)
        val munichStationsPath = bundle.pathForResource("munichStations", "json")
        val regensburgStationsPath = bundle.pathForResource("regensburgStations", "json")
        val combinedStations = mutableListOf<Station>()
        memScoped {
            munichStationsPath?.let {path ->
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                val stationsString = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, errorPtr.ptr)
                val munichStations = Json.decodeFromString<Stations>(stationsString!!)
                munichStations.features?.let {
                    combinedStations.addAll(it)
                }
            }
            regensburgStationsPath?.let {path ->
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                val stationsString = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, errorPtr.ptr)
                val regensburgStations = Json.decodeFromString<Stations>(stationsString!!)
                regensburgStations.features?.let {
                    combinedStations.addAll(it)
                }
            }
            if (combinedStations.isNotEmpty()) {
                combinedStations.filter { it.properties.street != null }
            }
        }
        return Stations(type = "", features = combinedStations)
    }

    actual val isDebug = Platform.isDebugBinary

    private class BundleMarker: NSObject() {
        companion object : NSObjectMeta()
    }
}