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

private const val ARG_OBJECT = "object"


class FavouritesFragment : Fragment() {
    lateinit var androidViewModel: WeatherForecastAndroidViewModel
    private lateinit var favouritesAdapter: FavouritesAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configureAndroidViewModel()
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

    private fun configureAndroidViewModel(){
        androidViewModel = ViewModelProvider(requireActivity()).get(WeatherForecastAndroidViewModel::class.java)
//        androidViewModel.favouriteLocationsForecast.observe(viewLifecycleOwner) {
//            favouritesAdapter = FavouritesAdapter(this)
//            viewPager.adapter = favouritesAdapter
//        }
    }

}

class FavouritesAdapter(val fragment: FavouritesFragment, private val favouriteLocations: List<WeatherDetailsContent>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = favouriteLocations.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WeatherDetailsFragment()
        fragment.weatherForecastData = favouriteLocations[position]
        return fragment
    }

    // I got "fragment already created". Don't know how it should work...
//    override fun getItemCount(): Int {
//        println("getItemsCount called")
//        if (fragment.androidViewModel.favouriteLocationsForecast.value != null) {
//            return fragment.androidViewModel.favouriteLocationsForecast.value!!.size
//        }
//        return 0
//    }
//
//    override fun createFragment(position: Int): Fragment {
//        println("createFragment called")
//        val weatherDetailsFragment = WeatherDetailsFragment()
//        if (fragment.androidViewModel.favouriteLocationsForecast.value != null && fragment.androidViewModel.favouriteLocationsForecast.value!!.size < position){
//            weatherDetailsFragment.updateFragmentContent(fragment.androidViewModel.favouriteLocationsForecast.value!!.elementAt(position))
//        }
//        return fragment
//    }
}
