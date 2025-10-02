package com.example.campusvibe.ui.story

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.campusvibe.databinding.ActivityStoryViewBinding

class StoryViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("IMAGE_URL")
        val username = intent.getStringExtra("USERNAME")

        Glide.with(this)
            .load(imageUrl)
            .into(binding.storyImageView)

        binding.storyUsernameTextView.text = username
    }
}
