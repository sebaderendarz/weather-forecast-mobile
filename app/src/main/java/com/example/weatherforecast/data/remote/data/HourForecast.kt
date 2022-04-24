package com.example.weatherforecast.data.remote.data

import kotlinx.serialization.Serializable

@Serializable
data class HourForecast(
    val dt: Long,
    val temp: Float,
    val wind_speed: Float,
    val weather: List<WeatherSummary>
)
