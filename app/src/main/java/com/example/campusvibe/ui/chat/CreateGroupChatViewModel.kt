package com.example.campusvibe.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.UserRepository
import com.example.campusvibe.model.User
import com.example.campusvibe.repository.ConversationsRepository
import kotlinx.coroutines.launch

class CreateGroupChatViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val conversationsRepository = ConversationsRepository()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _creationStatus = MutableLiveData<ChatCreationStatus>()
    val creationStatus: LiveData<ChatCreationStatus> = _creationStatus

    fun searchUsers(query: String) {
        viewModelScope.launch {
            userRepository.searchUsers(query).collect { userList ->
                _users.value = userList
            }
        }
    }

    fun createGroupChat(participants: List<String>, groupName: String) {
        viewModelScope.launch {
            _creationStatus.value = ChatCreationStatus.Loading
            try {
                val groupId = conversationsRepository.createGroupConversation(participants, groupName)
                _creationStatus.value = ChatCreationStatus.Success(groupId)
            } catch (e: Exception) {
                _creationStatus.value = ChatCreationStatus.Error(e.message ?: "Failed to create group")
            }
        }
    }

    fun createOneOnOneChat(otherUserId: String) {
        viewModelScope.launch {
            _creationStatus.value = ChatCreationStatus.Loading
            try {
                val conversationId = conversationsRepository.getOrCreateOneOnOneConversation(otherUserId)
                _creationStatus.value = ChatCreationStatus.Success(conversationId)
            } catch (e: Exception) {
                _creationStatus.value = ChatCreationStatus.Error(e.message ?: "Failed to create chat")
            }
        }
    }
}
