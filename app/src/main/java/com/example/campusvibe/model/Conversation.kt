package com.example.campusvibe.model

data class Conversation(
    var id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: Message? = null
)
