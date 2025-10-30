package com.example.campusvibe.Models

data class Notification(
    val id: String = "",
    val userId: String = "",
    val fromUserId: String = "",
    val type: String = "",
    val postId: String? = null,
    val timestamp: String = "",
    val text: String = "",
    var isRead: Boolean = false
)
