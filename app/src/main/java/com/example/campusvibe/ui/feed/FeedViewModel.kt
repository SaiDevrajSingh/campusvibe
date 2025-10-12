package com.example.campusvibe.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.FeedRepository
import com.example.campusvibe.data.StoryRepository
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.Story
import com.example.campusvibe.utils.LiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val feedRepository = FeedRepository()
    private val storyRepository = StoryRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    private val _showError = LiveEvent<Unit>()
    val showError: LiveData<Unit> = _showError

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    fun loadInitialData() {
        loadPosts()
        loadStories()
    }

    fun refreshData() {
        _isRefreshing.value = true
        loadPosts()
        loadStories()
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
                val storiesList = storyRepository.getStories()
                _stories.value = storiesList
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error loading stories", e)
            }
        }
    }
}
