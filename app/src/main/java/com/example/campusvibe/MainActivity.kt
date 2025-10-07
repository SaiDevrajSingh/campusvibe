package com.example.campusvibe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.campusvibe.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (FirebaseAuth.getInstance().currentUser == null) {
            navController.navigate(R.id.loginFragment)
        }

        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.addPostFragment -> {
                    navController.navigate(R.id.addPostFragment)
                    true
                }
                else -> {
                    // Let the NavController handle the other menu items
                    navController.navigate(item.itemId)
                    true
                }
            }
        }
    }
}
