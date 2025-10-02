package com.example.campusvibe.ui.feed

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.model.Post
import com.example.campusvibe.databinding.ItemPostBinding

class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.textViewUsername.text = post.username
        binding.textViewLikes.text = "${post.likes} likes"

        Glide.with(itemView.context)
            .load(post.imageUrl)
            .into(binding.imageViewPost)
    }
}

