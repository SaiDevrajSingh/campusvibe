package com.example.campusvibe.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.FeedRepository
import com.example.campusvibe.data.UserRepository
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.User
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val feedRepository = FeedRepository()
    private val userRepository = UserRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun loadPosts() {
        viewModelScope.launch {
            try {
                feedRepository.getPosts().collect { posts ->
                    _posts.value = posts
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                userRepository.getAllUsers().collect { users ->
                    _users.value = users
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

