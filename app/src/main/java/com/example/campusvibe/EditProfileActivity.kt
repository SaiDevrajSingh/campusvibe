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
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
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
            lifecycleScope.launch {
                val user = SupabaseClient.client.from("users").select {
                    filter {
                        eq("id", currentUserId)
                    }
                }.decodeSingleOrNull<User>()

                if (user != null) {
                    binding.name.setText(user.name)
                    binding.bio.setText(user.bio)
                    if (user.image != null) {
                        Picasso.get().load(user.image).into(binding.profileImage)
                    }
                }

                binding.saveButton.setOnClickListener {
                    val newName = binding.name.text.toString()
                    val newBio = binding.bio.text.toString()

                    lifecycleScope.launch {
                        SupabaseClient.client.postgrest.from("users").update(
                            buildJsonObject {
                                put("name", newName)
                                put("bio", newBio)
                            }
                        ) { filter {
                            eq("id", currentUserId)
                        } }
                        finish()
                    }
                }
            }
        }
    }
}