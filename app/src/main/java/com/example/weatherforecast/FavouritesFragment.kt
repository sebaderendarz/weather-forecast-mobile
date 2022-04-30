package com.example.weatherforecast

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherforecast.data.remote.data.WeatherDetailsContent


class FavouritesFragment : Fragment() {
    lateinit var androidViewModel: WeatherForecastAndroidViewModel
    lateinit var favouritesAdapter: FavouritesAdapter
    private lateinit var viewPager: ViewPager2
    lateinit var nothingToShowMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        androidViewModel =
            ViewModelProvider(requireActivity())[WeatherForecastAndroidViewModel::class.java]
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favouritesAdapter = if (androidViewModel.favouriteLocationsForecast.value != null) {
            FavouritesAdapter(
                this,
                androidViewModel.favouriteLocationsForecast.value!!.toMutableList()
            )
        } else {
            FavouritesAdapter(this, mutableListOf())
        }
        viewPager = view.findViewById(R.id.favouritesPager)
        viewPager.adapter = favouritesAdapter

        nothingToShowMessage = view.findViewById<TextView>(R.id.nothingToShowMessage)
        if (favouritesAdapter.favouriteLocations.size == 0) {
            nothingToShowMessage.visibility = View.VISIBLE
        }
    }
}

class FavouritesAdapter(
    val fragment: FavouritesFragment,
    val favouriteLocations: MutableList<WeatherDetailsContent>
) : FragmentStateAdapter(fragment) {

    private val pageIds = favouriteLocations.map { it.hashCode().toLong() }

    override fun createFragment(position: Int): Fragment {
        if (fragment.nothingToShowMessage.visibility == View.VISIBLE) {
            fragment.nothingToShowMessage.visibility = View.INVISIBLE
        }
        val fragment = WeatherDetailsFragment()
        fragment.weatherForecastData = favouriteLocations[position]
        return fragment
    }

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }

    override fun getItemCount(): Int = favouriteLocations.size

    override fun getItemId(position: Int): Long {
        return favouriteLocations[position].hashCode().toLong()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeFragment(locationName: String) {
        val index = getPositionByLocationName(locationName)
        if (index >= 0) {
            favouriteLocations.removeAt(index)
            notifyItemRangeChanged(index, favouriteLocations.size)
            notifyDataSetChanged()
        }
        if (favouriteLocations.size == 0) {
            fragment.nothingToShowMessage.visibility = View.VISIBLE
        }
    }

    private fun getPositionByLocationName(locationName: String): Int {
        for (index in favouriteLocations.indices) {
            if (favouriteLocations[index].locationName == locationName) {
                return index
            }
        }
        return -1;
    }
}
