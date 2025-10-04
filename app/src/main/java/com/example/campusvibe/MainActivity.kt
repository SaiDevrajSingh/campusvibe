package com.example.campusvibe

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.campusvibe.data.AuthViewModel
import com.example.campusvibe.databinding.ActivityMainBinding
import com.example.campusvibe.ui.create.AddContentBottomSheetFragment
import com.example.campusvibe.ui.home.HomeFragment
import com.example.campusvibe.ui.NotificationsFragment
import com.example.campusvibe.ui.ProfileFragment
import com.example.campusvibe.ui.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: androidx.navigation.NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Setup navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup ActionBar with NavController
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.searchFragment, R.id.notificationFragment, R.id.profileFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup bottom navigation with NavController
        binding.bottomNavigation.setupWithNavController(navController)
        authViewModel.user.observe(this) { user ->
            if (user != null) {
                // User is logged in, ensure we're on home screen
                if (navController.currentDestination?.id != R.id.homeFragment) {
                    navController.navigate(R.id.homeFragment)
                }
            }
            // Don't navigate to login here - let the navigation graph handle it via startDestination
        }

        // Handle create post button
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.createPostFragment -> {
                    AddContentBottomSheetFragment().show(supportFragmentManager, "add_content_sheet")
                    false // Don't select the item
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val profileItem = menu?.findItem(R.id.action_settings)
        val chatItem = menu?.findItem(R.id.action_chat)

        when (binding.bottomNavigation.selectedItemId) {
            R.id.profileFragment -> {
                profileItem?.isVisible = true
                chatItem?.isVisible = false
            }
            else -> {
                profileItem?.isVisible = false
                chatItem?.isVisible = true
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chat -> {
                navController.navigate(R.id.chatFragment)
                true
            }
            R.id.action_settings -> {
                navController.navigate(R.id.settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
