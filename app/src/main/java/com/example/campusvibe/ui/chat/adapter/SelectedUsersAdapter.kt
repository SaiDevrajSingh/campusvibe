package com.example.campusvibe.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.databinding.ItemSelectedUserBinding
import com.example.campusvibe.model.User

class SelectedUsersAdapter(private val onUserRemoved: (User) -> Unit) : RecyclerView.Adapter<SelectedUsersAdapter.SelectedUserViewHolder>() {

    private var users = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedUserViewHolder {
        val binding = ItemSelectedUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedUserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class SelectedUserViewHolder(private val binding: ItemSelectedUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.textViewUsername.text = user.username
            Glide.with(itemView.context)
                .load(user.profilePictureUrl ?: "https://via.placeholder.com/150")
                .circleCrop()
                .into(binding.imageViewProfile)

            binding.buttonRemove.setOnClickListener { onUserRemoved(user) }
        }
    }
}
