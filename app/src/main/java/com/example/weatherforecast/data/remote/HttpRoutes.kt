package com.example.weatherforecast.data.remote

object HttpRoutes {
    private const val API_KEY = "9ca270f2ad5976fc826d31384d7125b3"

    // Concatenate with city name -> q parameter.
    const val COORDINATES_BASE_URL =
        "https://api.openweathermap.org/geo/1.0/direct?appid=$API_KEY&limit=1"

    // Concatenate with lat, lon and units parameters.
    const val LOCATION_FORECAST_BASE_URL =
        "https://api.openweathermap.org/data/2.5/onecall?appid=$API_KEY&exclude=current,daily,minutely,alerts"
}
