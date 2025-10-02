package com.example.campusvibe.ui.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CreateViewModel(private val repository: StorageRepository) : ViewModel() {

    private val _uploadStatus = MutableLiveData<UploadStatus>()
    val uploadStatus: LiveData<UploadStatus> = _uploadStatus

    fun uploadPost(imageUri: Uri, caption: String, mediaType: String) {
        _uploadStatus.value = UploadStatus.Loading
        viewModelScope.launch {
            try {
                repository.uploadPost(imageUri, caption, mediaType)
                _uploadStatus.value = UploadStatus.Success
            } catch (e: Exception) {
                _uploadStatus.value = UploadStatus.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

class CreateViewModelFactory(private val repository: StorageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class UploadStatus {
    object Loading : UploadStatus()
    object Success : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}
