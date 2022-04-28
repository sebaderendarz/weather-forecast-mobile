package com.example.weatherforecast

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity() {
    lateinit var androidViewModel: WeatherForecastAndroidViewModel
    private val favouritesFragment = FavouritesFragment()
    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("activity on create called")

        androidViewModel = ViewModelProvider(this).get(WeatherForecastAndroidViewModel::class.java)

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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        // Clear focus on EditText field when clicked outside this field.
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun onSwipeRefresh(){
        println("SWIPE REFRESH INVOKED - fetch newer information about favourite locations")
        // add some logic here
        // how to reload activity to update data in fragments?
    }
}
