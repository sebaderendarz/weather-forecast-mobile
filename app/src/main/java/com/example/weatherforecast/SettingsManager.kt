package com.example.weatherforecast

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SettingsManager(context: Context) {
    private val KEY_DEFAULT_LOCATION = "defaultLocation";
    private val KEY_THERMAL_UNIT = "thermalUnit";
    private val KEY_ALLLOW_REFRESH_ON_SWIPE = "allowRefreshOnSwipe";
    private val KEY_NOT_SYNC_AUTOMATICALLY = "notSyncAutomatically";
    private val KEY_REFRESH_AFTER_PERIOD = "refreshAfterPeriod";
    private val KEY_SHOW_NOTIFICATIONS = "showNotifications";
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val defaultLocation: String?
        get() {
            val location = sharedPreferences.getString(KEY_DEFAULT_LOCATION, "Warsaw")
            if (location == "") return "Warsaw"
            return location
        }

    val thermalUnit: String?
        get() = sharedPreferences.getString(KEY_THERMAL_UNIT, "Celsius")

    val allowRefreshOnSwipe: Boolean
        get() = sharedPreferences.getBoolean(KEY_ALLLOW_REFRESH_ON_SWIPE, false)

    val notSyncAutomatically: Boolean
        get() = sharedPreferences.getBoolean(KEY_NOT_SYNC_AUTOMATICALLY, false)

    val refreshAfterPeriod: Int
        get() {
            val refreshPeriod = sharedPreferences.getString(KEY_REFRESH_AFTER_PERIOD, "4 hours")
            if (refreshPeriod == "8 hours") return 8
            if (refreshPeriod == "16 hours") return 16
            if (refreshPeriod == "24 hours") return 24
            return 4
        }
    val showNotifications: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_NOTIFICATIONS, false)
}
