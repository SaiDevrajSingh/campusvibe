package com.example.campusvibe.model

import java.util.Date

data class Story(
    val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val timestamp: Date = Date()
)
