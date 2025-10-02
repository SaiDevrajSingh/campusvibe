package com.example.campusvibe.model

import java.util.Date

data class Notification(
    val profileImageUrl: String = "",
    val message: String = "",
    val timestamp: Date = Date()
)

