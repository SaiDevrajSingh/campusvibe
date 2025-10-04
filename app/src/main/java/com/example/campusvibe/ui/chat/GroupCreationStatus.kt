package com.example.campusvibe.ui.chat

sealed class GroupCreationStatus {
    object Loading : GroupCreationStatus()
    data class Success(val groupId: String) : GroupCreationStatus()
    data class Error(val message: String) : GroupCreationStatus()
}
