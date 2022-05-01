package com.example.weatherforecast.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherforecast.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}