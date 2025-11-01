
package com.example.campusvibe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.campusvibe.databinding.ActivityPostDetailBinding

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postUrl = intent.getStringExtra("postUrl")
        val caption = intent.getStringExtra("caption")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")
        val username = intent.getStringExtra("username")

        Glide.with(this).load(postUrl).into(binding.postImage)
        binding.caption.text = caption
        Glide.with(this).load(profileImageUrl).into(binding.profileImage)
        binding.username.text = username
    }
}
