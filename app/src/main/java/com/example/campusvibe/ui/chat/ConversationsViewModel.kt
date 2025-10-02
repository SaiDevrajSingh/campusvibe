package com.example.campusvibe.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.model.Conversation
import com.example.campusvibe.repository.ConversationsRepository
import kotlinx.coroutines.launch

class ConversationsViewModel : ViewModel() {

    private val repository = ConversationsRepository()

    private val _conversations = MutableLiveData<List<Conversation>>()
    val conversations: LiveData<List<Conversation>> = _conversations

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            repository.getConversations().collect { conversationList ->
                _conversations.value = conversationList
            }
        }
    }
}


