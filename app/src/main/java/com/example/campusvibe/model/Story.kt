package com.example.campusvibe.model

import com.google.firebase.firestore.PropertyName

data class Story(
    var id: String = "",
    @get:PropertyName("user_id") @set:PropertyName("user_id") var userId: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
    var timestamp: Long = 0,
    var isPlaceholder: Boolean = false
)
