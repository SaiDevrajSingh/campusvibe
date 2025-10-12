package com.example.campusvibe.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ChatInfo(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: LastMessage? = null,
    val unreadCounts: Map<String, Long> = emptyMap(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class LastMessage(
    val text: String = "",
    val from: String = "",
    val timestamp: Timestamp? = null,
    val type: String = "text"
)

data class ChatMessage(
    var id: String = "",
    val text: String = "",
    val from: String = "",
    val to: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null,
    val type: String = "text",
    val mediaUrl: String? = null,
    val status: String? = "sent"
)
