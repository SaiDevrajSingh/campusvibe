package com.example.campusvibe.Post

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
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
        uri?.let {
            lifecycleScope.launch {
                imageUrl = uploadImage(this@PostActivity, it, "post_images")
                if (imageUrl != null) {
                    binding.selectImage.setImageURI(it)
                } else {
                    // Handle image upload failure
                }
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

                    currentUser?.let {
                        val post = Post(
                            postUrl = imageUrl!!,
                            caption = binding.caption.text.toString(),
                            userId = it.id
                        )

                        supabase.postgrest["posts"].insert(post)

                        startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("PostActivity", "Error creating post", e)
                }
            }
        }
    }
}