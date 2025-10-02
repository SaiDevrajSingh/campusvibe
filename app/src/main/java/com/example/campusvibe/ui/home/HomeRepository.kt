package com.example.campusvibe.ui.home

import com.example.campusvibe.model.Post
import com.example.campusvibe.data.PostWithUser
import com.example.campusvibe.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class HomeRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getFeedPosts(): List<PostWithUser> {
        val currentUser = auth.currentUser ?: return emptyList()

        val following = firestore.collection("users").document(currentUser.uid)
            .collection("following").get().await().documents.map { it.id }

        if (following.isEmpty()) {
            return emptyList()
        }

        val postDocuments = firestore.collection("posts")
            .whereIn("userId", following)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        val posts = postDocuments.toObjects(Post::class.java)

        val users = mutableMapOf<String, User>()

        val postWithUsers = posts.map {
            val user = users.getOrPut(it.userId) {
                firestore.collection("users").document(it.userId).get().await().toObject(User::class.java)!!
            }
            PostWithUser(it, user)
        }

        return postWithUsers
    }

}

