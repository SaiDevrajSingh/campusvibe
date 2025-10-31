package com.example.campusvibe.Models

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Post(
    @SerialName("id") 
    val id: Long? = null,
    
    @SerialName("user_id") 
    val userId: String,
    
    @SerialName("image_url") 
    val imageUrl: String,
    
    @SerialName("caption")
    val caption: String,
    
    @SerialName("created_at") 
    val createdAt: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date()),
    
    @SerialName("likes")
    @Serializable(with = IntOrEmptyArraySerializer::class)
    val likes: Int = 0
) {
    companion object {
        const val TABLE = "posts"
        const val ID = "id"
        const val USER_ID = "user_id"
        const val IMAGE_URL = "image_url"
        const val CAPTION = "caption"
        const val CREATED_AT = "created_at"
        const val LIKES = "likes"
    }
}

object IntOrEmptyArraySerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntOrEmptyArray", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }

    override fun deserialize(decoder: Decoder): Int {
        return when (val element = decoder as? JsonDecoder) {
            null -> decoder.decodeInt()
            else -> {
                val json = element.decodeJsonElement()
                when (json) {
                    is JsonArray -> 0 // Return 0 for empty array
                    is JsonPrimitive -> json.intOrNull ?: 0
                    else -> 0
                }
            }
        }
    }
}
