package com.example.campusvibe.ui.likes

import com.example.campusvibe.data.PostWithUser
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LikesRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getLikedPosts(): List<PostWithUser> {
        val currentUser = auth.currentUser ?: return emptyList()

        val likedPostsQuery = firestore.collection("posts")
            .whereArrayContains("likedBy", currentUser.uid)
            .get()
            .await()

        val posts = likedPostsQuery.toObjects(Post::class.java)
        val users = mutableMapOf<String, User>()

        val postWithUsers = posts.map { post ->
            val user = users.getOrPut(post.userId) {
                firestore.collection("users").document(post.userId).get().await().toObject(User::class.java)!!
            }
            PostWithUser(post, user)
        }

        return postWithUsers
    }
}

