package com.example.campusvibe.utils

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

import android.util.Log

object SupabaseClient {
    private const val TAG = "SupabaseClient"

    // Project API values from Supabase dashboard
    private const val SUPABASE_URL = "https://ufkrqjcfwnfvkfmspiza.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVma3JxamNmd25mdmtmbXNwaXphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE4NDgzMDksImV4cCI6MjA3NzQyNDMwOX0.BiEWKXGsnvehiYsk4PrzlRcPsWlBbpKGA4Cwl-ZGc0M"

    val client: SupabaseClient by lazy {
        try {
            createSupabaseClient(
                supabaseUrl = SUPABASE_URL,
                supabaseKey = SUPABASE_KEY
            ) {
                install(Auth)
                install(Postgrest)
                install(Realtime)
                install(Storage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Supabase client: ${e.message}")
            throw e // Re-throw to fail fast if initialization fails
        }
    }
}
