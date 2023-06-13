package com.example.emobilitychargingstations.domain.stations

@kotlinx.serialization.Serializable
data class StationGeoData(
    val type: String,
    val coordinates: Array<Double>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StationGeoData

        if (type != other.type) return false
        if (!coordinates.contentEquals(other.coordinates)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + coordinates.contentHashCode()
        return result
    }
}