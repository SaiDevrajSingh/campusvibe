package com.example.campusvibe.data

import com.example.campusvibe.model.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FeedRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    fun getPosts(): Flow<List<Post>> = callbackFlow {
        val subscription = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.toObjects(Post::class.java)
                    trySend(posts)
                } else {
                    close(Exception("Error fetching posts"))
                }
            }

        awaitClose { subscription.remove() }
    }

    suspend fun getPost(postId: String): Post? {
        return try {
            firestore.collection("posts").document(postId).get().await().toObject(Post::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun likePost(postId: String, liked: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val postRef = firestore.collection("posts").document(postId)
        if (liked) {
            postRef.update("likes", FieldValue.arrayUnion(userId)).await()
        } else {
            postRef.update("likes", FieldValue.arrayRemove(userId)).await()
        }
    }
}
