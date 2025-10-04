package com.example.campusvibe.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.databinding.ItemUserSearchBinding
import com.example.campusvibe.model.User

class UserSearchAdapter(private val onUserSelected: (User) -> Unit) : RecyclerView.Adapter<UserSearchAdapter.UserViewHolder>() {

    private var users = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val binding: ItemUserSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.usernameTextView.text = user.username
            Glide.with(itemView.context)
                .load(user.profilePictureUrl ?: "https://via.placeholder.com/150")
                .circleCrop()
                .into(binding.profileImageView)

            itemView.setOnClickListener { onUserSelected(user) }
        }
    }
}
