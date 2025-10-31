package com.example.campusvibe.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Reel(
    @SerialName("id")
    val id: Long? = null,
    
    @SerialName("user_id")
    val userId: String,
    
    @SerialName("video_url")
    val videoUrl: String,
    
    @SerialName("caption")
    val caption: String,
    
    @SerialName("created_at")
    val createdAt: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())
) {
    companion object {
        const val TABLE = "reels"
        const val ID = "id"
        const val USER_ID = "user_id"
        const val VIDEO_URL = "video_url"
        const val CAPTION = "caption"
        const val CREATED_AT = "created_at"
    }
}
