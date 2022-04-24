package com.example.weatherforecast.data.remote.data

import kotlinx.serialization.Serializable

@Serializable
data class NameToCoordinatesResponse(
    val name: String,
    val country: String,
    val state: String,
    val lat: Double,
    val lon: Double,
)
