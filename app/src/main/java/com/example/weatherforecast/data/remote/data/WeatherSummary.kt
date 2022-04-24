package com.example.weatherforecast.data.remote.data

import kotlinx.serialization.Serializable

@Serializable
data class WeatherSummary(
    val main: String,
    val icon: String,
)
