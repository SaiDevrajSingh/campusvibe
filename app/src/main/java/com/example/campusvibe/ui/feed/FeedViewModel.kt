package com.example.campusvibe.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.data.FeedRepository
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.Story
import com.example.campusvibe.data.StoryRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val feedRepository = FeedRepository()
    private val storyRepository = StoryRepository()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    val posts: LiveData<List<Post>> = feedRepository.getPosts().asLiveData()

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    init {
        deleteExpiredStories()
        getStories()
    }

    private fun getStories() {
        viewModelScope.launch {
            val allStories = storyRepository.getStories()
            val userStory = allStories.find { it.userId == currentUser?.uid }
            val otherStories = allStories.filter { it.userId != currentUser?.uid }

            val sortedStories = mutableListOf<Story>()
            if (userStory != null) {
                sortedStories.add(userStory)
                sortedStories.addAll(otherStories)
            } else {
                // If the user doesn't have a story, create a placeholder
                val placeholderStory = Story(userId = currentUser?.uid ?: "")
                sortedStories.add(placeholderStory)
                sortedStories.addAll(otherStories)
            }
            _stories.value = sortedStories
        }
    }

    private fun deleteExpiredStories() {
        viewModelScope.launch {
            storyRepository.deleteExpiredStories()
        }
    }
}

