package com.example.weatherforecast

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.remote.data.WeatherDetailsContent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream


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

    init {
        defaultLocation.value = takeDefaultLocationDataFromStorage()
        currentSearchLocation.value = defaultLocation.value
        favouriteLocationsForecast.value = takeFavouriteLocationsDataFromStorage()
    }

    override fun onCleared() {
        println("onCleared called")
        saveDefaultLocationDataToStorage()
        saveFavouriteLocationsDataToStorage()
        super.onCleared()
    }

    private fun saveDefaultLocationDataToStorage() {
        println("saveDefaultLocationDataToStorage called")
        if (defaultLocation != null) {
            try {
                app.openFileOutput(FILE_NAME_DEFAULT_LOCATION, MODE_PRIVATE).use { stream ->
                    Json.encodeToStream(defaultLocation, stream)
                }
            } catch (e: Exception) {
                println("saving default location to file failed")
            }
        }
    }

    private fun saveFavouriteLocationsDataToStorage() {
        println("saveFavouriteLocationsDataToStorage called")
        try {
            app.openFileOutput(FILE_NAME_FAVOURITE_LOCATIONS, MODE_PRIVATE).use { stream ->
                Json.encodeToStream(favouriteLocationsForecast, stream)
            }
        } catch (e: Exception) {
            println("saving favourite locations data failed")
        }
    }

    private fun takeDefaultLocationDataFromStorage(): WeatherDetailsContent? {
        return try {
            val files = app.filesDir.listFiles()
            val resultList: List<WeatherDetailsContent> =
                files?.filter { it.canRead() && it.isFile && it.name == FILE_NAME_DEFAULT_LOCATION }
                    ?.map {
                        Json.decodeFromStream<WeatherDetailsContent>(it.inputStream())
                    } ?: listOf()
            println("default results list")
            println(resultList)
            if (resultList.size != 1) {
                return null
            }
            return resultList[0]
        } catch (e: Exception) {
            println("fetching of default location data failed")
            null
        }
    }

    private fun takeFavouriteLocationsDataFromStorage(): List<WeatherDetailsContent> {
        return try {
            // Let's see if it will read all favourite locations at once :)
            // Empty list is read properly. How it goes when list is not empty?
            val files = app.filesDir.listFiles()
            val resultList: List<List<WeatherDetailsContent>> =
                files?.filter { it.canRead() && it.isFile && it.name == FILE_NAME_FAVOURITE_LOCATIONS }
                    ?.map {
                        Json.decodeFromStream<List<WeatherDetailsContent>>(it.inputStream())
                    } ?: listOf()
            println("favourite results list")
            println(resultList)
            if (resultList.size != 1) {
                return listOf()
            }
            return resultList[0]
        } catch (e: Exception) {
            println("fetching of default location data failed")
            listOf()
        }
    }

    // alternative way to read favourite locations data -> one by one
//    private fun takeFavouriteLocationsDataFromStorage(): List<WeatherDetailsContent>{
//        return try{
//            val files = app.filesDir.listFiles()
//            files?.filter{ it.canRead() && it.isFile && it.name != FILE_NAME_DEFAULT_LOCATION }?.map {
//                Json.decodeFromStream<List<WeatherDetailsContent>>(it.inputStream())
//            } ?: listOf()
//        }catch(e: Exception){
//            println("fetching of favourite locations data failed")
//            listOf()
//        }
//    }


    fun checkIfLocationInFavourites(locationName: String): Boolean{
        for (i in favouriteLocationsForecast.value?.indices!!){
            if (favouriteLocationsForecast.value!![i].locationName == locationName) return true
        }
        return false
    }

    fun addLocationToFavourites(locationForecast: WeatherDetailsContent){
        val filteredLocations: List<WeatherDetailsContent>? = favouriteLocationsForecast.value?.filter { item -> item.locationName != locationForecast.locationName }
        val mutableListLocations = filteredLocations?.toMutableList()
        mutableListLocations?.add(locationForecast)
        favouriteLocationsForecast.value = mutableListLocations?.toList()
        println(favouriteLocationsForecast.value)
    }

    fun removeLocationFromFavourites(locationForecast: WeatherDetailsContent){
        favouriteLocationsForecast.value = favouriteLocationsForecast.value?.filter { item -> item.locationName != locationForecast.locationName }
    }

    fun searchLocationForecast(location: String, requestType: RequestType){
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
                                    updateFavouriteLocations(weatherDetails)
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

    private fun updateFavouriteLocations(newLocationData: WeatherDetailsContent) {
        // filter through the list of favourite locations, remove if this location already exists and
        // add a new location.
    }

}




// TODO NOTE: "A resource failed to call close." error when calling saveFav/Def functions. Why?
// I get this only when save is called for the first time. During next saves there is no error like this.
