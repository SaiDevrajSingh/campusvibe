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
import com.example.campusvibe.model.User

class StoryAdapter(
    private val onStoryClick: (Story) -> Unit,
    private val onAddStoryClick: () -> Unit,
    private var currentUser: User?,
    private var currentUserStory: Story?
) : ListAdapter<Story, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    fun setCurrentUser(user: User?) {
        currentUser = user
        notifyItemChanged(0)
    }

    fun setCurrentUserStory(story: Story?) {
        currentUserStory = story
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        if (position == 0) {
            holder.bindCurrentUserStory()
        } else {
            holder.bind(getItem(position - 1))
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

        fun bindCurrentUserStory() {
            binding.storyUsernameTextView.text = "Your Story"
            binding.addStoryImageView.visibility = if (currentUserStory == null) View.VISIBLE else View.GONE

            Glide.with(itemView.context)
                .load(currentUser?.profilePictureUrl)
                .placeholder(R.drawable.ic_profile)
                .into(binding.storyImageView)

            if (currentUserStory != null) {
                binding.storyImageView.borderWidth = 2
                binding.storyImageView.borderColor =
                    ContextCompat.getColor(itemView.context, R.color.story_border)
            } else {
                binding.storyImageView.borderWidth = 0
            }

            itemView.setOnClickListener {
                if (currentUserStory != null) {
                    onStoryClick(currentUserStory!!)
                } else {
                    onAddStoryClick()
                }
            }
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
