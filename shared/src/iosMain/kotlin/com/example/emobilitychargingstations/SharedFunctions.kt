package com.example.emobilitychargingstations

import com.example.emobilitychargingstations.models.Stations
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile
import platform.darwin.NSObject
import platform.darwin.NSObjectMeta

actual class SharedFunctions {
    @OptIn(ExperimentalForeignApi::class)
    actual fun getStationsFromJson(): Stations? {
        var stations: Stations? = null
        val bundle = NSBundle.bundleForClass(BundleMarker)
        val munichStationsPath = bundle.pathForResource("munichStations", "json")
        munichStationsPath?.let {
            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                val stationsString = NSString.stringWithContentsOfFile(it, NSUTF8StringEncoding, errorPtr.ptr)
                stations = Json.decodeFromString<Stations>(stationsString!!)
            }
        }
        return stations
    }

    private class BundleMarker: NSObject() {
        companion object : NSObjectMeta()
    }
}