package com.example.campusvibe.Models

import java.util.Date

data class Message(
    val id: Long? = null,
    val senderId: String,
    val text: String,
    val timestamp: String = "",
    val conversationId: String,
    val createdAt: String = ""
)
