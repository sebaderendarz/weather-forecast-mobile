package com.example.weatherforecast.data.remote.data

import kotlinx.serialization.Serializable

@Serializable
data class WeatherDetailsContent(
    val forecast: ForecastResponse,
    val locationName: String,
    val units: String
)
