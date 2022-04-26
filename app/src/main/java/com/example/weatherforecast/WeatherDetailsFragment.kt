package com.example.weatherforecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.weatherforecast.data.remote.data.HourForecast
import com.example.weatherforecast.data.remote.data.WeatherDetailsContent
import com.example.weatherforecast.databinding.FragmentWeatherDetailsBinding
import com.squareup.picasso.Picasso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val ARG_OBJECT = "object"

// TODO
// 4. Default value should be the same as search results, not some dummy data.
// 5. Missing "Add to favourites" logic.


class WeatherDetailsFragment : Fragment() {

    private val WEATHER_ICON_BASE_URL = "https://openweathermap.org/img/wn/"
    private var _binding: FragmentWeatherDetailsBinding? = null;
    private val binding get() = _binding!!
    private val weatherForecastKey = "weatherForecast"
    private var weatherForecastData: WeatherDetailsContent?  = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)

        binding.favouriteButton.setOnClickListener {
            println("Favourite button clicked")
            println(weatherForecastData)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //println("new view created")
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val temperature: TextView = view.findViewById(R.id.temperature)
            temperature.text = getInt(ARG_OBJECT).toString() + "F"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //println("on save instance state in weather forecast details")
        if (weatherForecastData != null) {
            val data = Json.encodeToString(weatherForecastData)
            outState.putString(weatherForecastKey, data)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        //println("on restore instance state in weather forecast details")
        if (savedInstanceState != null) {
            val stringData = savedInstanceState.getString(weatherForecastKey)
            if (stringData != null && stringData.isNotEmpty()){
                weatherForecastData = Json.decodeFromString<WeatherDetailsContent>(stringData)
                if (weatherForecastData != null) {
                    updateFragmentContent(weatherForecastData!!)
                }
            }
        }
    }

    fun updateFragmentContent(weatherDetails: WeatherDetailsContent) {
        if (weatherDetails.forecast.hourly.size < 25) {
            // TODO display error message toast?
            return;
        }
        weatherForecastData = weatherDetails
        binding.cityName.text = weatherDetails.locationName
        setTemperature(weatherDetails.forecast.hourly[0].temp, weatherDetails.units)
        setImage(weatherDetails.forecast.hourly[0].weather[0].icon)
        binding.weatherType.text = weatherDetails.forecast.hourly[0].weather[0].main
        updateHourlyForecast(weatherDetails.forecast.hourly.subList(1, 25), weatherDetails.forecast.timezone_offset, weatherDetails.units)
    }

    private fun setTemperature(temperature: Float, units: String) {
        val roundedTemp = (temperature*10).toInt().toFloat()/10
        when (units) {
            "imperial" -> ("${roundedTemp}F").also { binding.temperature.text = it }
            "metric" -> ("${roundedTemp}°C").also { binding.temperature.text = it }
            else -> ("${roundedTemp}K").also { binding.temperature.text = it }
        }
    }

    private fun setImage(imageName: String) {
        val url = "$WEATHER_ICON_BASE_URL$imageName@2x.png"
        Picasso.get().load(url).into(binding.imageView)
    }

    private fun updateHourlyForecast(hourlyWeatherDetails: List<HourForecast>, timezoneOffset: Int, units: String) {
        for (index in hourlyWeatherDetails.indices) {
            val id: Int = resources.getIdentifier(
                "fragmentContainerView" + (index + 1).toString(),
                "id",
                requireActivity().packageName
            )
            val fragment: WeatherHourDetailsFragment =
                childFragmentManager.findFragmentById(id) as WeatherHourDetailsFragment
            fragment.updateFragmentContent(hourlyWeatherDetails[index], timezoneOffset, units)
        }
    }
}
