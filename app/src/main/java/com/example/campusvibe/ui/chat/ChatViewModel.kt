package com.example.campusvibe.ui.chat

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.model.Message
import com.example.campusvibe.repository.ChatRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ChatViewModel(private val conversationId: String) : ViewModel() {

    private val repository = ChatRepository()
    private val auth = Firebase.auth

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    init {
        listenForMessages()
    }

    private fun listenForMessages() {
        viewModelScope.launch {
            repository.getMessages(conversationId).collect { messageList ->
                _messages.value = messageList
            }
        }
    }

    fun sendMediaMessage(mediaUri: Uri, mediaType: String) {
        val senderId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.sendMediaMessage(conversationId, mediaUri, mediaType, senderId)
        }
    }
