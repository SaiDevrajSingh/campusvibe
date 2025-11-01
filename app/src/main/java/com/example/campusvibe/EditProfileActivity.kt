package com.example.campusvibe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.Models.User
import com.example.campusvibe.databinding.ActivityEditProfileBinding
import com.example.campusvibe.utils.SupabaseClient
import com.squareup.picasso.Picasso
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id

        if (currentUserId != null) {
            // Fetch and display initial data
            lifecycleScope.launch {
                val users = SupabaseClient.client.from("users").select {
                    filter { eq("id", currentUserId) }
                }.decodeList<User>()
                val user = users.firstOrNull()

                user?.let {
                    binding.name.setText(it.name)
                    binding.bio.setText(it.bio)
                    if (it.image != null) {
                        Picasso.get().load(it.image).into(binding.profileImage)
                    }
                }
            }

            // Set up save button listener
            binding.saveButton.setOnClickListener {
                val newName = binding.name.text.toString()
                val newBio = binding.bio.text.toString()

                lifecycleScope.launch {
                    // Perform the update in a background thread and wait for it to complete
                    withContext(Dispatchers.IO) {
                        SupabaseClient.client.from("users").update(
                            buildJsonObject {
                                put("name", newName)
                                put("bio", newBio)
                            }
                        ) {
                            filter { eq("id", currentUserId) }
                        }
                    }

                    // Finish the activity on the main thread after the update is complete
                    finish()
                }
            }
        }
    }
}
