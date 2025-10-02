package com.example.campusvibe.model

import com.google.firebase.firestore.PropertyName

data class Post(
    var id: String = "",
    @get:PropertyName("user_id") @set:PropertyName("user_id") var userId: String = "",
    var username: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
    var caption: String = "",
    var timestamp: Long = 0,
    var likes: Int = 0,
    @get:PropertyName("liked_by") @set:PropertyName("liked_by") var likedBy: List<String> = emptyList()
)
