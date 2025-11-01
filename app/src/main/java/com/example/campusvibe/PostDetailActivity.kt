
package com.example.campusvibe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campusvibe.databinding.ActivityPostDetailBinding
import com.squareup.picasso.Picasso

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

        Picasso.get().load(postUrl).into(binding.postImage)
        binding.caption.text = caption
        Picasso.get().load(profileImageUrl).into(binding.profileImage)
        binding.username.text = username
    }
}
