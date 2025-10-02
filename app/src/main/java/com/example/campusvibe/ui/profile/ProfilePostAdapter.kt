package com.example.campusvibe.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.databinding.ItemPostGridBinding

class ProfilePostAdapter(private val posts: List<String>) : RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount() = posts.size

    inner class PostViewHolder(private val binding: ItemPostGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: String) {
            // For now, we'll just load a placeholder image
            Glide.with(itemView.context)
                .load("https://picsum.photos/200/300?random=$adapterPosition")
                .into(binding.imageViewPost)
        }
    }
}


