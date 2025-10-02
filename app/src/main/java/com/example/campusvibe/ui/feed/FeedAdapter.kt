package com.example.campusvibe.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.data.FeedRepository
import com.example.campusvibe.data.UserRepository
import com.example.campusvibe.databinding.ItemFeedPostBinding
import com.example.campusvibe.model.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedAdapter : ListAdapter<Post, FeedAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    inner class PostViewHolder(private val binding: ItemFeedPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val userRepository = UserRepository()
        private val feedRepository = FeedRepository()
        private val auth = Firebase.auth
        private val coroutineScope = CoroutineScope(Dispatchers.Main)

        fun bind(post: Post) {
            binding.captionTextView.text = post.caption

            Glide.with(itemView.context)
                .load(post.imageUrl)
                .into(binding.postImageView)

            coroutineScope.launch {
                val user = withContext(Dispatchers.IO) {
                    userRepository.getUser(post.userId)
                }
                user?.let {
                    binding.usernameTextView.text = it.username
                    Glide.with(itemView.context)
                        .load(it.profileImageUrl)
                        .placeholder(R.drawable.ic_profile)
                        .into(binding.userProfileImageView)
                }
            }

            val likesCount = post.likes
            binding.likesCountTextView.text = "$likesCount likes"

            val currentUser = auth.currentUser
            val isLiked = currentUser != null && post.likedBy.contains(currentUser.uid)
            binding.likeButton.setImageResource(
                if (isLiked) R.drawable.ic_liked else R.drawable.ic_likes
            )

            binding.likeButton.setOnClickListener {
                val currentIsLiked = currentUser != null && post.likedBy.contains(currentUser.uid)
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        feedRepository.likePost(post.id, !currentIsLiked)
                    }
                }
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
