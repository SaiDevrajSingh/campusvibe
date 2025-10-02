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
            Glide.with(itemView.context)
                .load(story.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.storyImageView)
            binding.storyUsernameTextView.text = story.userId
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
