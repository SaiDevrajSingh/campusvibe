package com.example.campusvibe

import android.app.Application
import android.util.Log
import com.example.campusvibe.utils.SupabaseClient as AppSupabaseClient

class CampusVibeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Supabase client
        try {
            AppSupabaseClient.initialize(this)
            Log.d("CampusVibeApp", "Supabase client initialized successfully")
        } catch (e: Exception) {
            Log.e("CampusVibeApp", "Error initializing Supabase client", e)
        }
    }
}
