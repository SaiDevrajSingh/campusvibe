package com.example.campusvibe.Models

data class Post(
    val id: Long? = null,
    val postUrl: String,
    val caption: String,
    val userId: String,
    val createdAt: String = ""
)
