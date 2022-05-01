package com.example.weatherforecast

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


class SettingsManager(context: Context) {
    private val keyDefaultLocation = "defaultLocation";
    private val keyUnits = "units";
    private val keyAllowRefreshOnSwipeUp = "allowRefreshOnSwipeUp";
    private val keySyncAutomatically = "syncAutomatically";
    private val keyRefreshAfterPeriod = "refreshAfterPeriod";
    private val keyShowNotifications = "showNotifications";
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val defaultLocation: String
        get() {
            val locationName = sharedPreferences.getString(keyDefaultLocation, "Warsaw")
            return if (locationName != null && locationName.isNotEmpty()) locationName else "Warsaw"
        }

    val units: String
        get() = sharedPreferences.getString(keyUnits, "metric") ?: "metric"

    val allowRefreshOnSwipeUp: Boolean
        get() = sharedPreferences.getBoolean(keyAllowRefreshOnSwipeUp, false)

    val syncAutomatically: Boolean
        get() = sharedPreferences.getBoolean(keySyncAutomatically, false)

    val refreshAfterPeriod: Int
        get() {
            val refreshPeriod = sharedPreferences.getString(keyRefreshAfterPeriod, "4 hours")
            if (refreshPeriod == "8 hours") return 28800
            if (refreshPeriod == "16 hours") return 57600
            if (refreshPeriod == "24 hours") return 86400
            return 14400 // 4*3600
        }
    val showNotifications: Boolean
        get() = sharedPreferences.getBoolean(keyShowNotifications, false)
}
