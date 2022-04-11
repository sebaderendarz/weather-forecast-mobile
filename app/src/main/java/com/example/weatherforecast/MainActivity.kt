package com.example.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeItem) {
            // redirect to home fragment
            Toast.makeText(applicationContext, "Home was clicked", Toast.LENGTH_LONG).show()
        } else if (item.itemId == R.id.favouritesItem) {
            // redirect to favourites fragment
            Toast.makeText(applicationContext, "Favourites was clicked", Toast.LENGTH_LONG).show()
        } else if (item.itemId == R.id.settingItem){
            // redirect to settings fragment
            Toast.makeText(applicationContext, "Settings was clicked", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item);
    }
}