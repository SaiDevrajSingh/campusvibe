package com.example.campusvibe.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ItemUserBinding
import com.example.campusvibe.model.User

class SearchAdapter(private val users: List<User>, private val onUserClick: (User) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.usernameTextView.text = user.username
            if (!user.profileImageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(user.profileImageUrl)
                    .into(binding.profileImageView)
            } else {
                binding.profileImageView.setImageResource(R.drawable.ic_profile)
            }
            itemView.setOnClickListener { onUserClick(user) }
        }
    }
}


