package com.example.campusvibe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.campusvibe.Models.User
import com.example.campusvibe.databinding.ActivityEditProfileBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            fetchUserData()
        }

        binding.changePhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                updateProfile()
            }
        }
    }

    private suspend fun fetchUserData() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
        if (userId != null) {
            val user = SupabaseClient.client.postgrest["users"].select {
                filter {
                    eq("id", userId)
                }
            }.decodeSingle<User>()

            binding.nameEditText.setText(user.name)
            binding.bioEditText.setText(user.bio)
            if (!user.image.isNullOrEmpty()) {
                Glide.with(this).load(user.image).into(binding.profileImage)
            }
        }
    }

    private suspend fun updateProfile() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id!!
        val name = binding.nameEditText.text.toString()
        val bio = binding.bioEditText.text.toString()

        var imageUrl: String? = null
        imageUri?.let {
            val imageFile = contentResolver.openInputStream(it)?.readBytes()
            if (imageFile != null) {
                val path = "profile_images/$userId"
                SupabaseClient.client.storage["profile_images"].upload(path, imageFile, upsert = true)
                imageUrl = SupabaseClient.client.storage["profile_images"].publicUrl(path)
            }
        }

        val updates = mutableMapOf<String, Any?>()
        updates["name"] = name
        updates["bio"] = bio
        if (imageUrl != null) {
            updates["image"] = imageUrl
        }

        SupabaseClient.client.postgrest["users"].update(updates) {
            filter {
                eq("id", userId)
            }
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            binding.profileImage.setImageURI(imageUri)
        }
    }
}
