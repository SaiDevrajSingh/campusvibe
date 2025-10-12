package com.example.campusvibe.ui.chat

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _messages = MutableLiveData<List<Map<String, Any>>>()
    val messages: LiveData<List<Map<String, Any>>> = _messages

    fun getMessages(chatId: String): LiveData<List<Map<String, Any>>> {
        val messagesData = MutableLiveData<List<Map<String, Any>>>()
        viewModelScope.launch {
            repository.observeMessages(chatId).collect { snapshot ->
                val messagesList = snapshot.documents.map { it.data!! }
                messagesData.postValue(messagesList)
            }
        }
        return messagesData
    }

    fun sendMessage(chatId: String, messageText: String) {
        val message = mapOf(
            "senderId" to auth.currentUser!!.uid,
            "text" to messageText,
            "timestamp" to System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.sendMessage(chatId, message)
        }
    }
}
