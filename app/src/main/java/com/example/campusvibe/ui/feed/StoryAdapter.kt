package com.example.campusvibe.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ItemStoryBinding
import com.example.campusvibe.model.Story

class StoryAdapter(private val onStoryClick: (Story) -> Unit) :
    ListAdapter<Story, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    private val ADD_STORY_ITEM_POSITION = 0

    override fun submitList(list: List<Story>?) {
        val storiesWithAddButton = mutableListOf<Story>()
        storiesWithAddButton.add(
            Story(
                id = "add_story",
                userId = "Your Story",
                imageUrl = "",
                isPlaceholder = true
            )
        )
        if (list != null) {
            storiesWithAddButton.addAll(list)
        }
        super.submitList(storiesWithAddButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.storyUsernameTextView.text = story.userId

            if (bindingAdapterPosition == ADD_STORY_ITEM_POSITION) {
                binding.addStoryImageView.visibility = View.VISIBLE
                binding.storyImageView.setImageResource(R.drawable.ic_profile)
                binding.storyImageView.borderWidth = 0
            } else {
                binding.addStoryImageView.visibility = View.GONE
                Glide.with(itemView.context)
                    .load(story.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.storyImageView)

                binding.storyImageView.borderWidth = 2
                binding.storyImageView.borderColor =
                    ContextCompat.getColor(itemView.context, R.color.story_border)
            }

            itemView.setOnClickListener { onStoryClick(story) }
        }
    }
}

class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}
