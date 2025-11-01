package com.example.campusvibe

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campusvibe.databinding.ActivityReelDetailBinding
import com.squareup.picasso.Picasso

class ReelDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReelDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reelUrl = intent.getStringExtra("reelUrl")
        val caption = intent.getStringExtra("caption")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")
        val username = intent.getStringExtra("username")

        binding.videoView.setVideoURI(Uri.parse(reelUrl))
        binding.videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            binding.videoView.start()
        }

        binding.caption.text = caption
        Picasso.get().load(profileImageUrl).into(binding.profileImage)
        binding.username.text = username
    }
}
