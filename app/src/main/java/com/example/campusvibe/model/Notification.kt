package com.example.campusvibe.model

data class Notification(
    val id: String = "",
    val userId: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    val profileImageUrl: String = ""
)
