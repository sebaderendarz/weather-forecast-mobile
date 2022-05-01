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
import java.time.Instant


// Here we have data. Add observers in activity and fragments that will update view when values change.
// https://www.youtube.com/watch?v=whAVI1vTOko


class WeatherForecastAndroidViewModel(application: Application) : AndroidViewModel(application) {
    private val filenameDefaultLocation = "defaultLocation.json"
    private val filenameFavouriteLocations = "favouriteLocations.json"
    private val apiService = WeatherApiService.create()
    val settings: SettingsManager = SettingsManager(application)
    var defaultLocation = MutableLiveData<WeatherDetailsContent>()
    var currentSearchLocation = MutableLiveData<WeatherDetailsContent>()
    var favouriteLocationsForecast = MutableLiveData<List<WeatherDetailsContent>>()
    var favouritesUpdatingInProgress = MutableLiveData<Boolean>()
    private var favouritesToBeUpdated = 0

    init {
        defaultLocation.value = takeDefaultLocationDataFromStorage()
        currentSearchLocation.value = defaultLocation.value
        favouriteLocationsForecast.value = takeFavouriteLocationsDataFromStorage()
        favouritesUpdatingInProgress.value = false
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
                    filenameDefaultLocation,
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
                filenameFavouriteLocations,
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
                getApplication<Application>().openFileInput(filenameDefaultLocation)
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
                getApplication<Application>().openFileInput(filenameFavouriteLocations)
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
        val units = settings.units
        MainScope().launch {
            kotlin.runCatching {
                apiService.getCoordinates(location)
            }.onSuccess { it ->
                if (!it.isNullOrEmpty()) {
                    kotlin.runCatching {
                        locationName = it[0].name
                        apiService.getForecast(it[0].lat, it[0].lon, units)
                    }.onSuccess { it2 ->
                        if (it2 != null) {
                            val weatherDetails =
                                WeatherDetailsContent(it2, locationName, units)
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
                                    updateFavouritesUpdateState()
                                }
                            }
                        } else {
                            println("Request to get weather details returned incorrect data")
                            updateFavouritesUpdateState()
                        }
                    }.onFailure {
                        println("Request to get forecast failed")
                        updateFavouritesUpdateState()
                    }
                } else {
                    println("Request to get coordinates returned incorrect data")
                    updateFavouritesUpdateState()
                }
            }.onFailure {
                println("Request to get coordinates failed")
                updateFavouritesUpdateState()
            }
        }
    }

    private fun updateFavouritesUpdateState() {
        favouritesToBeUpdated -= 1
        if (favouritesToBeUpdated <= 0) {
            favouritesToBeUpdated = 0
            favouritesUpdatingInProgress.value = false
        }
    }

    fun refreshLocationsForecasts() {
        if (settings.defaultLocation != "") {
            searchLocationForecast(settings.defaultLocation, RequestType.DEFAULT)
        } else {
            searchLocationForecast(defaultLocation.value!!.locationName, RequestType.DEFAULT)
        }
        if (currentSearchLocation.value != null) {
            searchLocationForecast(currentSearchLocation.value!!.locationName, RequestType.SEARCH)
        }
        if (favouritesUpdatingInProgress.value == false
            && favouriteLocationsForecast.value != null
            && favouriteLocationsForecast.value!!.isNotEmpty()) {
            favouritesUpdatingInProgress.value = true
            favouritesToBeUpdated = favouriteLocationsForecast.value!!.size
            for (index in favouriteLocationsForecast.value!!.indices) {
                searchLocationForecast(
                    favouriteLocationsForecast.value!![index].locationName,
                    RequestType.FAVOURITE
                )
            }
        }
    }

    fun refreshLocationsForecastsIfAutoRefreshEnabled(){
        if (settings.syncAutomatically){
            if (favouriteLocationsForecast.value != null && favouriteLocationsForecast.value!!.isNotEmpty()){
                val currentTimestamp = Instant.now().epochSecond
                if (favouriteLocationsForecast.value!![0].forecast.hourly[0].dt + settings.refreshAfterPeriod < currentTimestamp){
                    refreshLocationsForecasts()
                }
            }
        }
    }
}
