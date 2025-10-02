package com.example.campusvibe.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.campusvibe.model.Conversation

class ConversationsViewModel : ViewModel() {

    private val repository = ConversationRepository()

    val conversations: LiveData<List<Conversation>> = repository.getConversations()
}


