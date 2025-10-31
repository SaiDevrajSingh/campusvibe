package com.example.campusvibe.Post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.HomeActivity
import com.example.campusvibe.Models.Post
import com.example.campusvibe.databinding.ActivityPostBinding
import com.example.campusvibe.utils.SupabaseClient
import com.example.campusvibe.utils.uploadImage
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class PostActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) {
            Log.e("PostActivity", "No image selected")
            return@registerForActivityResult
        }
        
        binding.progressBar.visibility = View.VISIBLE
        binding.postButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                imageUrl = uploadImage(this@PostActivity, uri, "posts")
                if (imageUrl != null) {
                    binding.selectImage.setImageURI(uri)
                    binding.selectImage.scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    Toast.makeText(
                        this@PostActivity, 
                        "Failed to upload image. Please try again.", 
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("PostActivity", "Error in image selection", e)
                Toast.makeText(
                    this@PostActivity, 
                    "Error: ${e.message}", 
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.postButton.isEnabled = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }
        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }
        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }
        binding.postButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val supabase = SupabaseClient.client
                    val currentUser = supabase.auth.currentUserOrNull()

                    if (imageUrl == null) {
                        Log.e("PostActivity", "No image selected")
                        return@launch
                    }
                    
                    currentUser?.let { user ->
                        val post = Post(
                            imageUrl = imageUrl!!,
                            caption = binding.caption.text.toString(),
                            userId = user.id
                        )

                        try {
                            val result = supabase.postgrest["posts"].insert(post)
                            Log.d("PostActivity", "Post created successfully")
                            
                            startActivity(Intent(this@PostActivity, HomeActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                            finish()
                        } catch (e: Exception) {
                            Log.e("PostActivity", "Error creating post", e)
                            // Show error to user
                            runOnUiThread {
                                binding.postButton.error = "Failed to create post: ${e.message}"
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PostActivity", "Error creating post", e)
                }
            }
        }
    }
}