package com.example.campusvibe.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Post(
    var id: String = "",
    @get:PropertyName("user_id") @set:PropertyName("user_id") var userId: String = "",
    var username: String = "",
    @get:PropertyName("user_profile_image_url") @set:PropertyName("user_profile_image_url") var userProfileImageUrl: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
    var caption: String = "",
    var timestamp: Date? = null,
    var likes: Int = 0,
    @get:PropertyName("liked_by") @set:PropertyName("liked_by") var likedBy: List<String> = emptyList()
)
