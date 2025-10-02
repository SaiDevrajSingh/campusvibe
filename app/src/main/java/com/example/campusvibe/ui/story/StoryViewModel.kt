package com.example.campusvibe.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campusvibe.model.Story
import com.example.campusvibe.data.StoryRepository
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {

    private val repository = StoryRepository()

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    fun getStories() {
        viewModelScope.launch {
            _stories.value = repository.getStories()
        }
    }

    fun uploadStory(story: Story) {
        viewModelScope.launch {
            repository.uploadStory(story)
            getStories()
        }
    }
}

