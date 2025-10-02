package com.example.campusvibe.model

import java.util.Date

data class Message(
    var id: String = "",
    val senderId: String = "",
    val text: String = "",
    val mediaUrl: String? = null,
    val mediaType: String? = null, // "image" or "video"
    val timestamp: Long = 0
)
