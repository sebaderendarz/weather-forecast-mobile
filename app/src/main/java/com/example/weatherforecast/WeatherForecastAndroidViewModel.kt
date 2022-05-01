package com.example.weatherforecast

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.remote.data.WeatherDetailsContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader


// Here we have data. Add observers in activity and fragments that will update view when values change.
// https://www.youtube.com/watch?v=whAVI1vTOko


class WeatherForecastAndroidViewModel(application: Application) : AndroidViewModel(application) {
    private val FILE_NAME_DEFAULT_LOCATION = "defaultLocation.json"
    private val FILE_NAME_FAVOURITE_LOCATIONS = "favouriteLocations.json"
    private val apiService = WeatherApiService.create()
    val settings: SettingsManager = SettingsManager(application)
    private val app: Application = application
    var defaultLocation = MutableLiveData<WeatherDetailsContent>()
    var currentSearchLocation = MutableLiveData<WeatherDetailsContent>()
    var favouriteLocationsForecast = MutableLiveData<List<WeatherDetailsContent>>()
    // var favouriteLocationsUpdated = MutableLiveData<Boolean>()

    init {
        defaultLocation.value = takeDefaultLocationDataFromStorage()
        currentSearchLocation.value = defaultLocation.value
        favouriteLocationsForecast.value = takeFavouriteLocationsDataFromStorage()
        // favouriteLocationsUpdated.value = false
    }

    fun saveDataToPrivateStorage() {
        println("saveDataToPrivateStorage called")
        saveDefaultLocationDataToStorage()
        saveFavouriteLocationsDataToStorage()
    }

    private fun saveDefaultLocationDataToStorage() {
        println("saveDefaultLocationDataToStorage called")
        if (defaultLocation.value != null) {
            try {
                val gson = Gson()
                val locationsForecast = gson.toJson(defaultLocation.value)
                val fileOutputStream = getApplication<Application>().openFileOutput(
                    FILE_NAME_DEFAULT_LOCATION,
                    MODE_PRIVATE
                )
                fileOutputStream.write(locationsForecast.toByteArray())
            } catch (e: Exception) {
                println("saving default location to file failed")
                println(e.message)
            }
        }
    }

    private fun saveFavouriteLocationsDataToStorage() {
        println("saveFavouriteLocationsDataToStorage called")
        try {
            val gson = Gson()
            val locationsForecast = gson.toJson(favouriteLocationsForecast.value)
            val fileOutputStream = getApplication<Application>().openFileOutput(
                FILE_NAME_FAVOURITE_LOCATIONS,
                MODE_PRIVATE
            )
            fileOutputStream.write(locationsForecast.toByteArray())
        } catch (e: Exception) {
            println("saving favourite locations data failed")
            println(e.message)
        }
    }

    private fun takeDefaultLocationDataFromStorage(): WeatherDetailsContent? {
        return try {
            val fileInputStream: FileInputStream? =
                getApplication<Application>().openFileInput(FILE_NAME_DEFAULT_LOCATION)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while (run {
                    text = bufferedReader.readLine()
                    text
                } != null) {
                stringBuilder.append(text)
            }
            println(stringBuilder.toString())
            val gson = Gson()
            return gson.fromJson<WeatherDetailsContent>(
                stringBuilder.toString(),
                WeatherDetailsContent::class.java
            )
        } catch (e: Exception) {
            println("fetching of default location data failed")
            null
        }
    }

    private fun takeFavouriteLocationsDataFromStorage(): List<WeatherDetailsContent> {
        return try {
            val fileInputStream: FileInputStream? =
                getApplication<Application>().openFileInput(FILE_NAME_FAVOURITE_LOCATIONS)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while (run {
                    text = bufferedReader.readLine()
                    text
                } != null) {
                stringBuilder.append(text)
            }
            println(stringBuilder.toString())
            val gson = Gson()
            val itemType = object : TypeToken<List<WeatherDetailsContent>>() {}.type
            return gson.fromJson<List<WeatherDetailsContent>>(stringBuilder.toString(), itemType)
        } catch (e: java.lang.Exception) {
            println(e.message)
            listOf()
        }
    }

    fun checkIfLocationInFavourites(locationName: String): Boolean {
        for (i in favouriteLocationsForecast.value?.indices!!) {
            if (favouriteLocationsForecast.value!![i].locationName == locationName) return true
        }
        return false
    }

    fun addLocationToFavourites(locationForecast: WeatherDetailsContent) {
        val filteredLocations: List<WeatherDetailsContent>? =
            favouriteLocationsForecast.value?.filter { item -> item.locationName != locationForecast.locationName }
        val mutableListLocations = filteredLocations?.toMutableList()
        mutableListLocations?.add(locationForecast)
        favouriteLocationsForecast.value = mutableListLocations?.toList()
        println(favouriteLocationsForecast.value)
    }

    fun removeLocationFromFavourites(locationForecast: WeatherDetailsContent) {
        favouriteLocationsForecast.value =
            favouriteLocationsForecast.value?.filter { item -> item.locationName != locationForecast.locationName }
    }

    fun searchLocationForecast(location: String, requestType: RequestType) {
        var locationName = location
        val thermalUnit = settings.thermalUnit
        MainScope().launch {
            kotlin.runCatching {
                //println("send getCoordinates request")
                apiService.getCoordinates(location)
            }.onSuccess { it ->
                //println(it)
                if (!it.isNullOrEmpty()) {
                    kotlin.runCatching {
                        locationName = it[0].name
                        apiService.getForecast(it[0].lat, it[0].lon, thermalUnit)
                    }.onSuccess { it2 ->
                        //println(it2)
                        if (it2 != null) {
                            val weatherDetails: WeatherDetailsContent =
                                WeatherDetailsContent(it2, locationName, thermalUnit)
                            when (requestType) {
                                RequestType.DEFAULT -> {
                                    defaultLocation.value = weatherDetails
                                }
                                RequestType.SEARCH -> {
                                    currentSearchLocation.value = weatherDetails
                                }
                                else -> {
                                    println("before addLocationToFavourites -> $location")
                                    addLocationToFavourites(weatherDetails)
                                    println("after addLocationToFavourites -> $location")
                                }
                            }
                        } else {
                            println("Request to get weather details returned incorrect data")
                        }
                    }.onFailure {
                        println("Request to get forecast failed")
                    }
                } else {
                    println("Request to get coordinates returned incorrect data")
                }
            }.onFailure {
                println("Request to get coordinates failed")
            }
        }
    }

    fun refreshLocationsForecasts() {
        if (settings.defaultLocation != ""){
            searchLocationForecast(settings.defaultLocation, RequestType.DEFAULT)
        } else {
            searchLocationForecast(defaultLocation.value!!.locationName, RequestType.DEFAULT)
        }
        if (currentSearchLocation.value != null){
            searchLocationForecast(currentSearchLocation.value!!.locationName, RequestType.SEARCH)
        }
        if (favouriteLocationsForecast.value != null){
            for (location in favouriteLocationsForecast.value!!){
                searchLocationForecast(location.locationName, RequestType.FAVOURITE)
                println("refreshLocationsForecasts -> refreshed location -> ${location.locationName}")
            }
        }
    }
}
