package com.example.weatherforecast

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


class SettingsManager(context: Context) {
    private val KEY_DEFAULT_LOCATION = "defaultLocation";
    private val KEY_THERMAL_UNIT = "units";
    private val KEY_ALLLOW_REFRESH_ON_SWIPE = "allowRefreshOnSwipe";
    private val KEY_SYNC_AUTOMATICALLY = "syncAutomatically";
    private val KEY_REFRESH_AFTER_PERIOD = "refreshAfterPeriod";
    private val KEY_SHOW_NOTIFICATIONS = "showNotifications";
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val defaultLocation: String
        get() {
            val locationName = sharedPreferences.getString(KEY_DEFAULT_LOCATION, "Warsaw")
            return if (locationName != null && locationName.isNotEmpty()) locationName else "Warsaw"
        }

    val units: String
        get() = sharedPreferences.getString(KEY_THERMAL_UNIT, "Celsius") ?: "Celsius"

    val allowRefreshOnSwipe: Boolean
        get() = sharedPreferences.getBoolean(KEY_ALLLOW_REFRESH_ON_SWIPE, false)

    val syncAutomatically: Boolean
        get() = sharedPreferences.getBoolean(KEY_SYNC_AUTOMATICALLY, false)

    val refreshAfterPeriod: Int
        get() {
            val refreshPeriod = sharedPreferences.getString(KEY_REFRESH_AFTER_PERIOD, "4 hours")
            if (refreshPeriod == "8 hours") return 28800
            if (refreshPeriod == "16 hours") return 57600
            if (refreshPeriod == "24 hours") return 86400
            return 14400 // 4*3600
        }
    val showNotifications: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_NOTIFICATIONS, false)
}
