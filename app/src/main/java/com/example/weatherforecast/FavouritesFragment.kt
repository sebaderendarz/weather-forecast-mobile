package com.example.weatherforecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherforecast.data.remote.data.WeatherDetailsContent


class FavouritesFragment : Fragment() {
    lateinit var androidViewModel: WeatherForecastAndroidViewModel
    private lateinit var favouritesAdapter: FavouritesAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        androidViewModel = ViewModelProvider(requireActivity()).get(WeatherForecastAndroidViewModel::class.java)
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favouritesAdapter = if (androidViewModel.favouriteLocationsForecast.value != null){
            FavouritesAdapter(this,
                androidViewModel.favouriteLocationsForecast.value!!
            )
        } else {
            FavouritesAdapter(this, listOf())
        }
        viewPager = view.findViewById(R.id.favouritesPager)
        viewPager.adapter = favouritesAdapter
    }
}

class FavouritesAdapter(val fragment: FavouritesFragment, private val favouriteLocations: List<WeatherDetailsContent>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = favouriteLocations.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WeatherDetailsFragment()
        fragment.weatherForecastData = favouriteLocations[position]
        return fragment
    }
}
