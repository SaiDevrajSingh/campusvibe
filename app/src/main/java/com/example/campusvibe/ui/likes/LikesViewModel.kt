package com.example.campusvibe.ui.likes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.PostWithUser
import kotlinx.coroutines.launch

class LikesViewModel : ViewModel() {

    private val _likedPosts = MutableLiveData<List<PostWithUser>>()
    val likedPosts: LiveData<List<PostWithUser>> = _likedPosts

    private val likesRepository = LikesRepository()

    init {
        fetchLikedPosts()
    }

    private fun fetchLikedPosts() {
        viewModelScope.launch {
            val posts = likesRepository.getLikedPosts()
            _likedPosts.value = posts
        }
    }
}

