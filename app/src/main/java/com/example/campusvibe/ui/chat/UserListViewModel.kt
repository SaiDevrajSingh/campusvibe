package com.example.campusvibe.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.UserRepository
import com.example.campusvibe.model.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserListViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _filteredUsers = MutableLiveData<List<User>>()
    val filteredUsers: LiveData<List<User>> = _filteredUsers

    init {
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect {
                _users.value = it
                _filteredUsers.value = it
            }
        }
    }

    fun searchUsers(query: String) {
        val currentUsers = _users.value ?: emptyList()
        if (query.isEmpty()) {
            _filteredUsers.value = currentUsers
        } else {
            _filteredUsers.value = currentUsers.filter {
                it.username.contains(query, ignoreCase = true) || it.name.contains(query, ignoreCase = true)
            }
        }
    }
}
