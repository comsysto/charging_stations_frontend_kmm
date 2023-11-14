package com.example.emobilitychargingstations.data.stations.api

import com.example.emobilitychargingstations.models.StationsResponseModel
import com.example.emobilitychargingstations.models.StationsRequestModel
import com.example.emobilitychargingstations.models.UserLocation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

class StationsApi {
    private val client = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }
    private val baseUrl = "http://192.168.8.131:8080/live-data-nobil"
    suspend fun requestStationsWithLocation(userLocation: UserLocation?): List<StationsResponseModel> {
        val requestBuilder = HttpRequestBuilder()
        var stationsRequest: StationsRequestModel? = null
        userLocation?.let {
            stationsRequest = StationsRequestModel(it.latitude, it.longitude, 10000, 10)
        }

//        requestBuilder.setBody(stationsRequest)
//        requestBuilder.build()
        val response = client.get(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(stationsRequest)
        }
        return response.body() as List<StationsResponseModel>
    }
}