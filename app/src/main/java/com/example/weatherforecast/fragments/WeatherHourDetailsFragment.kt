package com.example.weatherforecast.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.data.remote.data.HourForecast
import com.example.weatherforecast.databinding.FragmentWeatherHourDetailsBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class WeatherHourDetailsFragment : Fragment() {
    private val weatherIconBaseUrl = "https://openweathermap.org/img/wn/"
    private var _binding: FragmentWeatherHourDetailsBinding? = null;
    private val binding get() = _binding!!
    var hourForecast: HourForecast? = null;
    var tzOffset: Int? = null;
    var measureUnits: String? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherHourDetailsBinding.inflate(inflater, container, false)
        if (hourForecast == null || tzOffset == null || measureUnits == null) return binding.root
        updateFragmentContent(hourForecast!!, tzOffset!!, measureUnits!!)
        return binding.root
    }

    fun updateFragmentContent(weatherDetails: HourForecast, timezoneOffset: Int, units: String) {
        hourForecast = weatherDetails
        tzOffset = timezoneOffset
        measureUnits = units
        if (_binding != null) {
            setHour(weatherDetails.dt, timezoneOffset)
            setTemperature(weatherDetails.temp, units)
            setImage(weatherDetails.weather[0].icon)
            setWindSpeed(weatherDetails.wind_speed, units)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setHour(timestamp: Long, timezoneOffset: Int) {
        val sdf = SimpleDateFormat("h:00 a")
        binding.hour.text = sdf.format(Date((timestamp + timezoneOffset) * 1000))
    }

    private fun setTemperature(temperature: Float, units: String) {
        val roundedTemp = (temperature * 10).toInt().toFloat() / 10
        when (units) {
            "imperial" -> ("${roundedTemp}F").also { binding.temperature.text = it }
            "metric" -> ("${roundedTemp}°C").also { binding.temperature.text = it }
            else -> ("${roundedTemp}K").also { binding.temperature.text = it }
        }
    }

    private fun setImage(imageName: String) {
        val url = "$weatherIconBaseUrl$imageName@2x.png"
        Picasso.get().load(url).into(binding.imageView)
    }

    private fun setWindSpeed(windSpeed: Float, units: String) {
        val roundedSpeed = (windSpeed * 10).toInt().toFloat() / 10
        when (units) {
            "imperial" -> ("${roundedSpeed}mi/h").also { binding.windSpeed.text = it }
            "metric" -> ("${convertSpeedToKmPerHour(windSpeed)}km/h").also {
                binding.windSpeed.text = it
            }
            else -> ("${convertSpeedToKmPerHour(windSpeed)}km/h").also {
                binding.windSpeed.text = it
            }
        }
    }

    private fun convertSpeedToKmPerHour(speed: Float): String {
        return ((speed * 3600 / 100).toInt().toFloat() / 10).toString()
    }
}
