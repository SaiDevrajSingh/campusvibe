package com.example.campusvibe.Models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val image: String? = null,
    val bio: String? = null
)
