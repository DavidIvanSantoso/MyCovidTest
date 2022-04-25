package com.example.test2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val _bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val _navController = findNavController(R.id.nav_fragment)
        _bottomNavigation.setupWithNavController(_navController)

    }
}