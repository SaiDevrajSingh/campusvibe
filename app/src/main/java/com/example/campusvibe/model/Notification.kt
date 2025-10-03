package com.example.campusvibe.model

import java.util.Date

data class Notification(
    val id: String = "",
    val type: String = "", // "like", "comment", "follow", "mention"
    val senderId: String = "",
    val senderUsername: String = "",
    val senderProfileImageUrl: String = "",
    val recipientId: String = "",
    val postId: String? = null,
    val message: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = 0
)

