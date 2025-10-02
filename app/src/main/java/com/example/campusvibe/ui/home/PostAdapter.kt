package com.example.campusvibe.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.data.PostWithUser
import com.example.campusvibe.databinding.ItemPostBinding

class PostAdapter(var posts: List<PostWithUser>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postWithUser: PostWithUser) {
            binding.textViewUsername.text = postWithUser.user.username
            binding.textViewCaption.text = postWithUser.post.caption
            binding.textViewLikes.text = "${postWithUser.post.likes} likes"

            Glide.with(itemView.context)
                .load(postWithUser.user.profileImageUrl)
                .into(binding.imageViewProfile)

            Glide.with(itemView.context)
                .load(postWithUser.post.imageUrl)
                .into(binding.imageViewPost)
        }
    }
}

