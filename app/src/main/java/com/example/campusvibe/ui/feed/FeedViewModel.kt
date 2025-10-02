package com.example.campusvibe.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.FeedRepository
import com.example.campusvibe.model.Post
import com.example.campusvibe.utils.LiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val feedRepository = FeedRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _showError = LiveEvent<Unit>()
    val showError: LiveData<Unit> = _showError

    fun loadPosts() {
        viewModelScope.launch {
            feedRepository.getPosts()
                .catch { _showError.call() }
                .collect { _posts.value = it }
        }
    }
}
