package com.example.campusvibe.model

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val college: String = "",
    val userType: String = "",
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList(),
    val posts: Int = 0,
    val highlights: List<String> = emptyList(),
    val fcmToken: String = ""
)
