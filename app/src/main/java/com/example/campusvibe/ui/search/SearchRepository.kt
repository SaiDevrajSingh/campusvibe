package com.example.campusvibe.ui.search

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class SearchRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getPopularPosts(): List<com.example.campusvibe.model.Post> {
        val posts = firestore.collection("posts")
            .orderBy("likesCount", Query.Direction.DESCENDING)
            .limit(21)
            .get()
            .await()
        return posts.toObjects(com.example.campusvibe.model.Post::class.java)
    }

    suspend fun searchUsers(query: String): List<com.example.campusvibe.model.User> {
        val users = firestore.collection("users")
            .whereGreaterThanOrEqualTo("username", query)
            .whereLessThanOrEqualTo("username", query + "\uf8ff")
            .limit(20)
            .get()
            .await()
        return users.toObjects(com.example.campusvibe.model.User::class.java)
    }
}

