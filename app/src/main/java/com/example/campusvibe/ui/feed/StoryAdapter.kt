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

private const val VIEW_TYPE_ADD_STORY = 0
private const val VIEW_TYPE_STORY = 1

class StoryAdapter(private val onStoryClick: (Story) -> Unit, private val onAddStoryClick: () -> Unit) :
    ListAdapter<Story, RecyclerView.ViewHolder>(StoryDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ADD_STORY else VIEW_TYPE_STORY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if (viewType == VIEW_TYPE_ADD_STORY) {
            AddStoryViewHolder(binding)
        } else {
            StoryViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StoryViewHolder) {
            holder.bind(getItem(position -1))
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.storyUsernameTextView.text = story.username
            binding.addStoryImageView.visibility = View.GONE
            Glide.with(itemView.context)
                .load(story.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.storyImageView)

            binding.storyImageView.borderWidth = 2
            binding.storyImageView.borderColor =
                ContextCompat.getColor(itemView.context, R.color.story_border)

            itemView.setOnClickListener { onStoryClick(story) }
        }
    }

    inner class AddStoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.storyUsernameTextView.text = "Your Story"
            binding.storyImageView.setImageResource(R.drawable.ic_profile)
            binding.addStoryImageView.visibility = View.VISIBLE
            itemView.setOnClickListener { onAddStoryClick() }
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
