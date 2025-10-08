package com.example.campusvibe.ui.chat

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.model.Message
import com.example.campusvibe.data.ChatRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ChatViewModel(private val conversationId: String) : ViewModel() {

    private val repository = ChatRepository()
    private val auth = Firebase.auth

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    init {
        if (conversationId.isNotEmpty()) {
            listenForMessages()
        }
    }

    private fun listenForMessages() {
        viewModelScope.launch {
            repository.getMessages(conversationId).collect { messageList ->
                _messages.value = messageList
            }
        }
    }

    fun sendMessage(messageText: String) {
        val senderId = auth.currentUser?.uid ?: return
        val message = Message(
            senderId = senderId,
            text = messageText,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.sendMessage(conversationId, message)
        }
    }

    fun sendMediaMessage(mediaUri: Uri, mediaType: String) {
        val senderId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            repository.sendMediaMessage(conversationId, mediaUri, mediaType, senderId)
        }
    }
}
