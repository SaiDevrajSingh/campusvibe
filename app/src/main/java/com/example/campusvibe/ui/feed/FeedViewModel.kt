package com.example.campusvibe.ui.feed

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

    fun loadPosts() {
        viewModelScope.launch {
            feedRepository.getPosts()
                .catch { _showError.call() }
                .collect { _posts.value = it }
        }
    }

    fun loadStories() {
        viewModelScope.launch {
            try {
                val storiesList = storyRepository.getStories()

                // Add "Add Story" placeholder at the beginning
                val addStoryPlaceholder = Story(
                    id = "add_story",
                    userId = "add_story",
                    imageUrl = "", // Will be handled specially in adapter
                    timestamp = 0,
                    isPlaceholder = true
                )

                val storiesWithAddButton = listOf(addStoryPlaceholder) + storiesList
                _stories.value = storiesWithAddButton
            } catch (e: Exception) {
                // Handle story loading error silently for now
            }
        }
    }
}
