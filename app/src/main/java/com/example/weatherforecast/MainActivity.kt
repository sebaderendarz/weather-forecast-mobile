package com.example.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    val favouritesFragment = FavouritesFragment()
    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, homeFragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeItem) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, homeFragment)
                addToBackStack(null)
                commit()
            }
            // redirect to home fragment
            // Toast.makeText(applicationContext, "Home was clicked", Toast.LENGTH_LONG).show()
        } else if (item.itemId == R.id.favouritesItem) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, favouritesFragment)
                addToBackStack(null)
                commit()
            }
            // redirect to favourites fragment
            // Toast.makeText(applicationContext, "Favourites was clicked", Toast.LENGTH_LONG).show()
        } else if (item.itemId == R.id.settingItem){
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, settingsFragment)
                addToBackStack(null)
                commit()
            }
            // redirect to settings fragment
            // Toast.makeText(applicationContext, "Settings was clicked", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item);
    }
}