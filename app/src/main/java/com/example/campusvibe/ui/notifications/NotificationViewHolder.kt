package com.example.campusvibe.ui.notifications

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.databinding.ItemNotificationBinding
import com.example.campusvibe.model.Notification

class NotificationViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(notification: Notification) {
        binding.notificationMessage.text = notification.message
        Glide.with(itemView.context)
            .load(notification.profileImageUrl)
            .into(binding.notificationProfileImage)
    }
}

