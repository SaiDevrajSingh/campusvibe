package com.example.campusvibe.Models

import java.util.Date

data class Conversation(
    val id: Long? = null,
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTimestamp: String = "",
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val createdAt: String = ""
)
