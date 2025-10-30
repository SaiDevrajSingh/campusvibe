package com.example.campusvibe.Post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.campusvibe.databinding.ActivityStoryBinding
import com.bumptech.glide.Glide

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyImageUrl = intent.getStringExtra("story_image_url")

        Glide.with(this).load(storyImageUrl).into(binding.storyImage)
    }
}
