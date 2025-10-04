package com.example.campusvibe.ui.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateViewModel : ViewModel() {

    private val repository = StorageRepository()

    private val _uploadStatus = MutableLiveData<UploadStatus>()
    val uploadStatus: LiveData<UploadStatus> = _uploadStatus

    suspend fun uploadPost(imageUri: Uri, caption: String, mediaType: String, context: Context) {
        _uploadStatus.value = UploadStatus.Loading
        try {
            repository.uploadPost(imageUri, caption, mediaType, context)
            _uploadStatus.value = UploadStatus.Success
        } catch (e: Exception) {
            _uploadStatus.value = UploadStatus.Error(e.message ?: "An unknown error occurred")
        }
    }
}
