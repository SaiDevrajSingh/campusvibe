package com.example.campusvibe.ui.feed

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ItemFeedPostBinding
import com.example.campusvibe.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {

    private var posts: List<Post> = emptyList()
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    fun submitList(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    inner class PostViewHolder(private val binding: ItemFeedPostBinding) : RecyclerView.ViewHolder(binding.root) {

        private val gestureDetector = GestureDetector(itemView.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                toggleLike(posts[adapterPosition])
                return true
            }
        })

        fun bind(post: Post) {
            binding.usernameTextView.text = post.username
            binding.captionTextView.text = post.caption
            binding.likesCountTextView.text = "${post.likes} likes"

            Glide.with(itemView.context)
                .load(post.imageUrl)
                .into(binding.postImageView)

            binding.userProfileImageView.setImageResource(R.drawable.ic_profile)

            updateLikeButton(post.isLiked)

            binding.likeButton.setOnClickListener {
                toggleLike(post)
            }

            binding.postImageView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        }

        private fun updateLikeButton(isLiked: Boolean) {
            if (isLiked) {
                binding.likeButton.setImageResource(R.drawable.ic_liked)
            } else {
                binding.likeButton.setImageResource(R.drawable.ic_likes)
            }
        }

        private fun toggleLike(post: Post) {
            val postRef = firestore.collection("posts").document(post.id)
            val userId = currentUser?.uid ?: return

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef).toObject(Post::class.java)!!
                val isCurrentlyLiked = snapshot.isLiked
                val newLikesCount = if (isCurrentlyLiked) snapshot.likes - 1 else snapshot.likes + 1

                transaction.update(postRef, "liked", !isCurrentlyLiked)
                transaction.update(postRef, "likes", newLikesCount)

                null
            }
        }

        private fun vibrate() {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = itemView.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                itemView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        }
    }
}

