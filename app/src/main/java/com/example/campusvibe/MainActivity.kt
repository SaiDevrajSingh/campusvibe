package com.example.campusvibe

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.campusvibe.databinding.ActivityMainBinding
import com.example.campusvibe.ui.chat.ConversationsActivity
import com.example.campusvibe.ui.create.AddContentBottomSheetFragment
import com.example.campusvibe.ui.home.HomeFragment
import com.example.campusvibe.ui.notifications.NotificationsFragment
import com.example.campusvibe.ui.profile.ProfileFragment
import com.example.campusvibe.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_add) {
                AddContentBottomSheetFragment().show(supportFragmentManager, "add_content_sheet")
                return@setOnNavigationItemSelectedListener false // Don't select the item
            }

            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                    supportActionBar?.title = "CampusVibe"
                    invalidateOptionsMenu() // Refresh the menu
                }
                R.id.nav_search -> {
                    selectedFragment = SearchFragment()
                    supportActionBar?.title = "Search"
                    invalidateOptionsMenu()
                }
                R.id.nav_notifications -> {
                    selectedFragment = NotificationsFragment()
                    supportActionBar?.title = "Notifications"
                    invalidateOptionsMenu()
                }
                R.id.nav_profile -> {
                    selectedFragment = ProfileFragment()
                    supportActionBar?.title = "devrajs"
                    invalidateOptionsMenu()
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
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

        if (binding.bottomNavigation.selectedItemId == R.id.nav_profile) {
            profileItem?.isVisible = true
            chatItem?.isVisible = false
        } else {
            profileItem?.isVisible = false
            chatItem?.isVisible = true
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chat -> {
                startActivity(Intent(this, ConversationsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}


