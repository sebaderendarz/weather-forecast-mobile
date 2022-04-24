package com.example.weatherforecast.data.remote

import com.example.weatherforecast.data.remote.data.ForecastResponse
import com.example.weatherforecast.data.remote.data.NameToCoordinatesResponse
import io.ktor.client.*
import io.ktor.client.request.*

class WeatherApiServiceImpl(
    private val client: HttpClient
): WeatherApiService {

    override suspend fun getCoordinates(locationName: String): List<NameToCoordinatesResponse>? {
        return try {
            client.get {
                url(HttpRoutes.COORDINATES_BASE_URL)
                parameter("q", locationName)
            }
         } catch(e: Exception) {
            null
        }
    }

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        units: String
    ): ForecastResponse? {
        return try {
            client.get {
                url(HttpRoutes.LOCATION_FORECAST_BASE_URL)
                parameter("lat", latitude)
                parameter("lon", longitude)
                parameter("units", units)
            }
        } catch(e: Exception) {
            null
        }
    }
}