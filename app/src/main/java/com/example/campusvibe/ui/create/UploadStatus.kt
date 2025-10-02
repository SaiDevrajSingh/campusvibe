package com.example.campusvibe.ui.create

sealed class UploadStatus {
    object Loading : UploadStatus()
    object Success : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}
