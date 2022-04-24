package com.example.weatherforecast.data.remote.data

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val lat: Double,
    val lon: Double,
    val hourly: List<HourForecast>
)
