package com.example.campusvibe

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.campusvibe.data.AuthViewModel
import com.example.campusvibe.databinding.ActivityMainBinding
import com.example.campusvibe.ui.create.AddContentBottomSheetFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        authViewModel.user.observe(this) { user ->
            val navController = findNavController(R.id.nav_host_fragment)
            if (user == null) {
                // User is not logged in, navigate to the login screen
                if (navController.currentDestination?.id != R.id.loginFragment) {
                    navController.navigate(R.id.loginFragment)
                }
            } else {
                // User is logged in, set up the main UI
                setupMainUI()
            }
        }
    }

    private fun setupMainUI() {
        val navController = findNavController(R.id.nav_host_fragment)

        // We are not using setupWithNavController because we have a custom item
        // that doesn't navigate. Instead, we handle item selection manually.
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.createPostFragment) {
                AddContentBottomSheetFragment().show(supportFragmentManager, "add_content_sheet")
                false // Return false: don't select the item
            } else {
                // For all other items, let NavigationUI handle the navigation
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }

        // Keep the BottomNavigationView selection in sync with the NavController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true

            // Update the toolbar title
            supportActionBar?.title = when (destination.id) {
                R.id.homeFragment -> "CampusVibe"
                R.id.searchFragment -> "Search"
                R.id.reelsFragment -> "Reels"
                R.id.profileFragment -> "devrajs"
                else -> "CampusVibe"
            }
            invalidateOptionsMenu() // Refresh the menu
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val profileItem = menu?.findItem(R.id.action_settings)
        val chatItem = menu?.findItem(R.id.action_chat)

        val currentDestinationId = findNavController(R.id.nav_host_fragment).currentDestination?.id

        if (currentDestinationId == R.id.profileFragment) {
            profileItem?.isVisible = true
            chatItem?.isVisible = false
        } else {
            profileItem?.isVisible = false
            chatItem?.isVisible = true
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
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
}
