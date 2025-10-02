package com.example.campusvibe.ui.reels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.model.Post
import kotlinx.coroutines.launch

class ReelsViewModel : ViewModel() {

    private val repository = ReelsRepository()

    private val _reels = MutableLiveData<List<Post>>()
    val reels: LiveData<List<Post>> = _reels

    fun fetchReels() {
        viewModelScope.launch {
            _reels.value = repository.getReels()
        }
    }
}

