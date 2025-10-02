package com.example.campusvibe.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ItemStoryBinding
import com.example.campusvibe.model.Story

class StoryAdapter(private val onStoryClick: (Story) -> Unit) :
    ListAdapter<Story, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            if (story.isPlaceholder && story.id == "add_story") {
                // Show "Add Story" placeholder
                binding.storyImageView.setImageResource(R.drawable.ic_add_circle_outline_black_24dp)
                binding.storyUsernameTextView.text = "Your Story"
            } else {
                // Load story image in circular format (Instagram style)
                Glide.with(itemView.context)
                    .load(story.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .circleCrop()
                    .into(binding.storyImageView)

                // For now, show user ID as username (TODO: fetch actual username)
                binding.storyUsernameTextView.text = story.userId
            }

            // Add visual indicator for different story types
            binding.storyImageView.borderWidth = when {
                story.isPlaceholder -> 3
                else -> 2
            }
            binding.storyImageView.borderColor = when {
                story.isPlaceholder -> R.color.colorPrimary
                else -> R.color.colorAccent
            }

            itemView.setOnClickListener { onStoryClick(story) }
        }
    }
}

class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.userId == newItem.userId && oldItem.timestamp == newItem.timestamp
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}
