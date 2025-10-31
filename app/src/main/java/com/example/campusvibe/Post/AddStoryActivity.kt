package com.example.campusvibe.Post

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.Models.Story
import com.example.campusvibe.databinding.ActivityAddStoryBinding
import com.example.campusvibe.utils.SupabaseClient
import com.example.campusvibe.utils.uploadImage
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var imageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.storyImageView.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.storyImageView.setOnClickListener {
            selectImage()
        }

        binding.postStoryButton.setOnClickListener {
            uploadStory()
        }
    }

    private fun selectImage() {
        selectImageLauncher.launch("image/*")
    }

    private fun uploadStory() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val supabase = SupabaseClient.client
                val currentUser = supabase.auth.currentUserOrNull()

                currentUser?.let {
                    val imageUrl = uploadImage(this@AddStoryActivity, imageUri!!, "story_images")
                    if (imageUrl != null) {
                        val story = Story(
                            userId = it.id,
                            imageUrl = imageUrl,
                            timestamp = System.currentTimeMillis().toString()
                        )
                        supabase.postgrest["stories"].insert(story)
                        Toast.makeText(this@AddStoryActivity, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddStoryActivity, "Failed to upload story", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("AddStoryActivity", "Failed to upload story", e)
                Toast.makeText(this@AddStoryActivity, "Failed to upload story", Toast.LENGTH_SHORT).show()
            }
        }
    }
}