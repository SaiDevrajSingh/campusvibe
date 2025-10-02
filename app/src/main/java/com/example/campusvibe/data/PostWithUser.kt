package com.example.campusvibe.data

import com.example.campusvibe.model.Post
import com.example.campusvibe.model.User

data class PostWithUser(
    val post: Post,
    val user: User
)

