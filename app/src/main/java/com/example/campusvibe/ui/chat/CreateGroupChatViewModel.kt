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

    private val _groupCreationStatus = MutableLiveData<GroupCreationStatus>()
    val groupCreationStatus: LiveData<GroupCreationStatus> = _groupCreationStatus

    fun searchUsers(query: String) {
        viewModelScope.launch {
            userRepository.searchUsers(query).collect { userList ->
                _users.value = userList
            }
        }
    }

    fun createGroupChat(participants: List<String>, groupName: String) {
        viewModelScope.launch {
            _groupCreationStatus.value = GroupCreationStatus.Loading
            try {
                conversationsRepository.createGroupConversation(participants, groupName)
                _groupCreationStatus.value = GroupCreationStatus.Success
            } catch (e: Exception) {
                _groupCreationStatus.value = GroupCreationStatus.Error(e.message ?: "Failed to create group")
            }
        }
    }
}

sealed class GroupCreationStatus {
    object Loading : GroupCreationStatus()
    object Success : GroupCreationStatus()
    data class Error(val message: String) : GroupCreationStatus()
}
