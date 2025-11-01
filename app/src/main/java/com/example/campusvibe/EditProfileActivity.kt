package com.example.campusvibe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.databinding.ActivityEditProfileBinding
import com.example.campusvibe.utils.SupabaseClient
import com.squareup.picasso.Picasso
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
                val user = SupabaseClient.client.postgrest.from("users").select() {
                    filter {
                        eq("id", currentUserId)
                    }
                }.data

                val jsonObject = Json.parseToJsonElement(user).jsonObject
                val name = jsonObject["name"]?.jsonPrimitive?.content
                val bio = jsonObject["bio"]?.jsonPrimitive?.content
                val imageUrl = jsonObject["image"]?.jsonPrimitive?.content

                binding.name.setText(name)
                binding.bio.setText(bio)
                if (imageUrl != null) {
                    Picasso.get().load(imageUrl).into(binding.profileImage)
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