package com.example.campusvibe.model

data class Conversation(
    var id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: Message? = null,
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val timestamp: Long = 0
)
