package com.example.campusvibe.ui.feed

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.model.Story
import com.example.campusvibe.databinding.ItemStoryBinding

class StoryViewHolder(private val binding: ItemStoryBinding, private val onClick: (Story) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(story: Story) {
        binding.root.setOnClickListener { onClick(story) }
        // We need to get the username from the user ID. This will be a future implementation.
        // binding.storyUsernameTextView.text = story.userId
        Glide.with(binding.root.context)
            .load(story.imageUrl)
            .into(binding.storyImageView)
    }
}

