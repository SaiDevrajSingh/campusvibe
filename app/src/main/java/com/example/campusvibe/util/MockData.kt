package com.example.campusvibe.util

import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

fun addMockData() {
    val firestore = FirebaseFirestore.getInstance()
    val postsCollection = firestore.collection("posts")

    val mockPosts = listOf(
        mapOf(
            "user_id" to "1",
            "username" to "John Doe",
            "user_profile_image_url" to "https://picsum.photos/200",
            "image_url" to "https://picsum.photos/400",
            "caption" to "My first post!",
            "timestamp" to Date(),
            "likes" to 0,
            "liked_by" to emptyList<String>()
        ),
        mapOf(
            "user_id" to "2",
            "username" to "Jane Smith",
            "user_profile_image_url" to "https://picsum.photos/201",
            "image_url" to "https://picsum.photos/401",
            "caption" to "Loving CampusVibe!",
            "timestamp" to Date(),
            "likes" to 0,
            "liked_by" to emptyList<String>()
        )
    )

    mockPosts.forEach { post ->
        postsCollection.add(post)
    }
}
