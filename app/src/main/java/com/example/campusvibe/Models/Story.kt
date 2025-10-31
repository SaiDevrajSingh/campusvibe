package com.example.campusvibe.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Story(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("image_url") val imageUrl: String = "",
    @SerialName("created_at") val timestamp: String = "",
    @SerialName("username") val username: String = "",
    @SerialName("profile_image") val profileImage: String? = null
)
