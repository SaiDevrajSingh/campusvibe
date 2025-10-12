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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel @Inject constructor(private val repo: ChatRepository) : ViewModel() {

    private val _chatId = MutableStateFlow<String?>(null)
    fun setChat(chatId: String) { _chatId.value = chatId }

    // real-time messages
    val messages: StateFlow<QuerySnapshot?> = _chatId.filterNotNull().flatMapLatest { chatId ->
        repo.observeMessages(chatId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun sendMessage(chatId: String, msg: ChatMessage) {
        viewModelScope.launch {
            repo.sendMessage(chatId, msg)
        }
    }

    fun markAsRead(chatId: String, myUid: String) {
        viewModelScope.launch {
            repo.markChatAsRead(chatId, myUid)
        }
    }
}
