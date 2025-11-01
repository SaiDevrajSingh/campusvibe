package com.example.campusvibe.utils

import android.content.Context
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json

object SupabaseClient {
    private const val TAG = "SupabaseClient"

    private const val SUPABASE_URL = "https://ufkrqjcfwnfvkfmspiza.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVma3JxamNmd25mdmtmbXNwaXphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE4NDgzMDksImV4cCI6MjA3NzQyNDMwOX0.BiEWKXGsnvehiYsk4PrzlRcPsWlBbpKGA4Cwl-ZGc0M"

    private var _client: SupabaseClient? = null
    val client: SupabaseClient
        get() = _client ?: throw IllegalStateException("Supabase client not initialized")

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Synchronized
    fun initialize(context: Context) {
        if (_client == null) {
            try {
                _client = createSupabaseClient(
                    supabaseUrl = SUPABASE_URL,
                    supabaseKey = SUPABASE_KEY
                ) {
                    install(Auth) {
                        scheme = "campusvibe"
                        host = "login"
                    }

                    install(Postgrest) {
                        serializer = KotlinXSerializer(jsonConfig)
                    }

                    install(Realtime)
                    install(Storage)
                }
                Log.d(TAG, "Supabase client initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Supabase client", e)
                throw e
            }
        }
    }

    fun getPostImageUrl(path: String): String {
        return client.storage["posts"].publicUrl(path)
    }

    fun getAvatarUrl(path: String): String {
        return client.storage["avatars"].publicUrl(path)
    }
}
