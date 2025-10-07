package com.example.campusvibe.ui.chat

sealed class ChatCreationStatus {
    data class Success(val conversationId: String) : ChatCreationStatus()
    data class Error(val message: String) : ChatCreationStatus()
    object Loading : ChatCreationStatus()
}
