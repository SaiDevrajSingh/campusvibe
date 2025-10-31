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

    // Base URL for Supabase
    private const val SUPABASE_URL = "https://ufkrqjcfwnfvkfmspiza.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVma3JxamNmd25mdmtmbXNwaXphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE4NDgzMDksImV4cCI6MjA3NzQyNDMwOX0.BiEWKXGsnvehiYsk4PrzlRcPsWlBbpKGA4Cwl-ZGc0M"

    // Storage bucket names
    const val STORAGE_BUCKET_POSTS = "posts"
    const val STORAGE_BUCKET_AVATARS = "profile_pictures"
    const val STORAGE_BUCKET_REELS = "reels"
    const val STORAGE_BUCKET_STORIES = "story_media"

    private var _client: SupabaseClient? = null

    val client: SupabaseClient
        get() = _client ?: throw IllegalStateException("SupabaseClient not initialized. Call initialize() first.")

    /**
     * Initialize the Supabase client
     */
    fun initialize(context: Context) {
        if (_client == null) {
            try {
                // Create JSON configuration that ignores unknown keys
                val jsonConfig = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                }

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

    /**
     * Get the public URL for a file in storage
     */
    fun getPublicFileUrl(bucket: String, path: String): String {
        val url = "$SUPABASE_URL/storage/v1/object/public/$bucket/$path"
        Log.d(TAG, "Generated storage URL - Bucket: $bucket, Path: $path, Full URL: $url")
        return url
    }

    /**
     * Get the public URL for a post image
     */
    fun getPostImageUrl(fileName: String): String {
        return getPublicFileUrl(STORAGE_BUCKET_POSTS, fileName)
    }

    /**
     * Get the public URL for a user avatar
     */
    fun getAvatarUrl(fileName: String): String {
        return "$SUPABASE_URL/storage/v1/object/public/$STORAGE_BUCKET_AVATARS/$fileName"
    }

    /**
     * Get the public URL for a reel video
     */
    fun getReelUrl(fileName: String): String {
        return "$SUPABASE_URL/storage/v1/object/public/$STORAGE_BUCKET_REELS/$fileName"
    }

    /**
     * Get the public URL for a story image
     */
    fun getStoryUrl(fileName: String): String {
        return "$SUPABASE_URL/storage/v1/object/public/$STORAGE_BUCKET_STORIES/$fileName"
    }
}
