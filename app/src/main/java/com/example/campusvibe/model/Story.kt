package com.example.campusvibe.model

data class Story(
    val id: String = "",
    val userId: String = "",
    var username: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isPlaceholder: Boolean = false
)