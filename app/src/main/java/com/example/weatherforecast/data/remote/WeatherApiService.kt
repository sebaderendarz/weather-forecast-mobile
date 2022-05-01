package com.example.weatherforecast.data.remote

import com.example.weatherforecast.data.remote.data.ForecastResponse
import com.example.weatherforecast.data.remote.data.NameToCoordinatesResponse
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

interface WeatherApiService {

    suspend fun getCoordinates(locationName: String): List<NameToCoordinatesResponse>?

    suspend fun getForecast(latitude: Double, longitude: Double, units: String): ForecastResponse?

    companion object {
        fun create(): WeatherApiService {
            return WeatherApiServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                            ignoreUnknownKeys = true
                        })
                    }
                }
            )
        }
    }
}