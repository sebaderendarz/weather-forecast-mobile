package com.example.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherforecast.data.remote.WeatherApiService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// TODO
// One data object in the main activity. Pass this data through constructor
// to fragments or add methods allowing you to take data stored
// in activity from fragments or update this data from fragments (e.g no longer in favourites).
// You can call sth like "getActivity"? from fragment to get access to activity
// to be able to call methods in activity, but make sure if it is the easiest way.


class MainActivity : AppCompatActivity() {
    private val favouritesFragment = FavouritesFragment()
    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()
    var settings: SettingsManager? = null
    private val mainScope = MainScope()
    private val service = WeatherApiService.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settings = SettingsManager(applicationContext)

        mainScope.launch {
            kotlin.runCatching {
                service.getForecast(52.231956,21.006725, "metric")
            }.onSuccess {
                println(it)
            }.onFailure {
                println("Request failed")
            }
        }

        if (savedInstanceState == null) {
            // https://stackoverflow.com/questions/48806201/why-is-oncreateview-in-fragment-called-twice-after-device-rotation-in-android
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, homeFragment)
                commit()
            }
        }

        val swipe: SwipeRefreshLayout = findViewById(R.id.refreshLayout)
        swipe.setOnRefreshListener {
            this.onSwipeRefresh()
            swipe.isRefreshing = false
        }
        // onSwipeRefresh() -> here you can invoke swipe refresh when app is opened for the first time
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeItem -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, homeFragment)
                    addToBackStack(null)
                    commit()
                }
            }
            R.id.favouritesItem -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, favouritesFragment)
                    addToBackStack(null)
                    commit()
                }
            }
            R.id.settingItem -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, settingsFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private fun onSwipeRefresh(){
        println("SWIPE REFRESH INVOKED - fetch newer information about favourite locations")
        // add some logic here
        // how to reload activity to update data in fragments?
    }
}
