package com.example.campusvibe.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.FeedRepository
import com.example.campusvibe.data.StoryRepository
import com.example.campusvibe.data.UserRepository
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.Story
import com.example.campusvibe.model.User
import com.example.campusvibe.utils.LiveEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val feedRepository = FeedRepository()
    private val storyRepository = StoryRepository()
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _followingStories = MutableLiveData<List<Story>>()
    val followingStories: LiveData<List<Story>> = _followingStories

    private val _currentUserStory = MutableLiveData<Story?>()
    val currentUserStory: LiveData<Story?> = _currentUserStory

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _showError = LiveEvent<Unit>()
    val showError: LiveData<Unit> = _showError

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    fun loadInitialData() {
        loadPosts()
        loadStories()
        loadCurrentUser()
    }

    fun refreshData() {
        _isRefreshing.value = true
        loadPosts()
        loadStories()
        loadCurrentUser()
        _isRefreshing.value = false
    }

    private fun loadPosts() {
        viewModelScope.launch {
            feedRepository.getPosts()
                .catch { _showError.call() }
                .collect { _posts.value = it }
        }
    }

    private fun loadStories() {
        viewModelScope.launch {
            try {
                val allStories = storyRepository.getStories()
                val currentUserId = auth.currentUser?.uid
                _currentUserStory.value = allStories.find { it.userId == currentUserId }
                _followingStories.value = allStories.filter { it.userId != currentUserId }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error loading stories", e)
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid
                if (currentUserId != null) {
                    _currentUser.value = userRepository.getUser(currentUserId)
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error loading current user", e)
            }
        }
    }
}
