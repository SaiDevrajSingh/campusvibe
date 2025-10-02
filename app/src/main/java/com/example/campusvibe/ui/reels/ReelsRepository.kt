package com.example.campusvibe.ui.reels

import com.example.campusvibe.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ReelsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getReels(): List<Post> {
        return firestore.collection("posts")
            .whereEqualTo("type", "video")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Post::class.java)
    }
}

