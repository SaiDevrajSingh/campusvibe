package com.example.campusvibe.utils

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClient {

    private const val SUPABASE_URL = "https://ufkrqjcfwnfvkfmspiza.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVma3JxamNmd25mdmtmbXNwaXphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE4NDgzMDksImV4cCI6MjA3NzQyNDMwOX0.BiEWKXGsnvehiYsk4PrzlRcPsWlBbpKGA4Cwl-ZGc0M"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth) {
            scheme = "campusvibe"
            host = "login"
        }
        install(Postgrest)
        install(Realtime)
        install(Storage)
    }
}
