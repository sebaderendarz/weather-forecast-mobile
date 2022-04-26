package com.example.weatherforecast

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.remote.data.WeatherDetailsContent
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

// TODO
// 1. Add logic based on settings value. Displaying notifications etc.

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null;
    private val binding get() = _binding!!
    private val apiService = WeatherApiService.create()
    private val mainScope = MainScope()
    private val inputTextKey = "textInputText"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.textInputLayout.setEndIconOnClickListener {
            val imm: InputMethodManager = binding.textInputText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive){
                imm.hideSoftInputFromWindow(binding.textInputText.windowToken, 0)
            }
            binding.textInputLayout.clearFocus()
            searchLocationForecast()
        }

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction().apply {
                replace(R.id.weatherDetailsLayout, WeatherDetailsFragment())
                commit()
            }
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Using view instead of binding.textInputText solved issue with NullPointerException when
        // 1. go to favourites 2. go back to home 3. change to horizontal 4. search for location
        // 5. change back to vertical 6. get exception
        // But I don't like this solution. It is a workaround.
        val view = activity?.findViewById<TextInputEditText>(R.id.textInputText)
        outState.putString(inputTextKey, view?.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val savedInput = savedInstanceState?.getString(inputTextKey)
        if (savedInput != null){
            binding.textInputText.setText(savedInput)
        }
        binding.textInputText.setSelection(binding.textInputText.length())
    }

    private fun searchLocationForecast(){
        var locationName = binding.textInputText.text.toString()
        if (locationName != ""){
            mainScope.launch {
                kotlin.runCatching {
                    //println("send getCoordinates request")
                    apiService.getCoordinates(locationName)
                }.onSuccess { it ->
                    //println(it)
                    if (!it.isNullOrEmpty()) {
                        kotlin.runCatching {
                            locationName = it[0].name
                            apiService.getForecast(it[0].lat, it[0].lon, "metric")
                        }.onSuccess { it2 ->
                            //println(it2)
                            if (it2 != null) {
                                val weatherDetails: WeatherDetailsContent =
                                    WeatherDetailsContent(it2, locationName, "metric")
                                val fragment: WeatherDetailsFragment = childFragmentManager.findFragmentById(R.id.weatherDetailsLayout) as WeatherDetailsFragment
                                fragment.updateFragmentContent(weatherDetails)
                            }
                            else {
                                println("Request to get weather details returned incorrect data")
                            }
                        }.onFailure {
                            println("Request to get forecast failed")
                        }
                    }
                    else {
                        println("Request to get coordinates returned incorrect data")
                    }
                }.onFailure {
                    println("Request to get coordinates failed")
                }
            }
        }
    }
}
