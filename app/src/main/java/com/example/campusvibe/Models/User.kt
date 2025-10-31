package com.example.campusvibe.Models

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class User(
    @SerialName("id")
    val id: String,
    
    @SerialName("name")
    val name: String? = null,
    
    @SerialName("username")
    val username: String? = null,
    
    @SerialName("email")
    val email: String? = null,
    
    @SerialName("image")
    val image: String? = null,
    
    @SerialName("bio")
    val bio: String? = null,
    
    @SerialName("provider")
    val provider: String? = null,
    
    @SerialName("is_online")
    val isOnline: Boolean = false,
    
    @SerialName("last_seen")
    val lastSeen: String? = null
) {
    companion object {
        // Custom JSON configuration to ignore unknown keys
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
    }
}
