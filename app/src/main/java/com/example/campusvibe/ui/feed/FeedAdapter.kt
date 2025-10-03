package com.example.campusvibe.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import android.content.Intent

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

            val likesCount = post.likedBy.size
            binding.likesCountTextView.text = "$likesCount likes"

            val currentUser = auth.currentUser
            val isLiked = currentUser != null && post.likedBy.contains(currentUser.uid)
            binding.likeButton.setImageResource(
                if (isLiked) R.drawable.ic_liked else R.drawable.ic_likes
            )

            binding.commentButton.setOnClickListener {
                // Navigate to comments screen for this post
                val intent = Intent(itemView.context, CommentsActivity::class.java)
                intent.putExtra("postId", post.id)
                intent.putExtra("postImageUrl", post.imageUrl)
                intent.putExtra("postCaption", post.caption)
                itemView.context.startActivity(intent)
            }

            binding.shareButton.setOnClickListener {
                // Share post functionality
                val shareText = "Check out this post: ${post.caption}\n\nShared from CampusVibe"
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                itemView.context.startActivity(Intent.createChooser(shareIntent, "Share post"))
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
