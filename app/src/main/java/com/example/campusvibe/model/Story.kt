package com.example.campusvibe.model

data class Story(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0,
    val isPlaceholder: Boolean = false
)