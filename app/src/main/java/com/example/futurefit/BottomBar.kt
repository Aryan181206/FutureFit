package com.example.futurefit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.futurefit.Fragments.ExploreFrag
import com.example.futurefit.Fragments.HomeFrag
import com.example.futurefit.Fragments.ProfileFrag
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomBar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_bar)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Set default fragment
        replaceFragment(HomeFrag())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFrag())
                    true
                }
                R.id.explore -> {
                    replaceFragment(ExploreFrag())
                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFrag())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
