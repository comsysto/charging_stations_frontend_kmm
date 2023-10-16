package com.example.emobilitychargingstations.data.stations.api

import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.UserLocation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.setBody

class StationsApi {
    private val client = HttpClient()
    suspend fun requestStationsWithLocation(userLocation: UserLocation): List<Station> {
//        val requestBuilder = HttpRequestBuilder()
//        requestBuilder.setBody(userLocation)
//        requestBuilder.build()
//        val response = client.get("TODO: URL FOR GETTING STATIONS") {
//            setBody(userLocation)
//        }
//        val listOfStations = response.body<List<Station>>()
        return listOf()
    }
}