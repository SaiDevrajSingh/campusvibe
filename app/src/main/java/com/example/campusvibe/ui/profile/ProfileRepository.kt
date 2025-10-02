package com.example.campusvibe.ui.profile

import android.net.Uri
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProfileRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun getUser(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserPosts(userId: String): List<Post> {
        return try {
            val querySnapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            querySnapshot.toObjects(Post::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun followUser(userId: String) {
        val currentUser = auth.currentUser ?: return
        firestore.collection("users").document(currentUser.uid).collection("following").document(userId).set(mapOf("userId" to userId)).await()
        firestore.collection("users").document(userId).collection("followers").document(currentUser.uid).set(mapOf("userId" to currentUser.uid)).await()
    }

    suspend fun unfollowUser(userId: String) {
        val currentUser = auth.currentUser ?: return
        firestore.collection("users").document(currentUser.uid).collection("following").document(userId).delete().await()
        firestore.collection("users").document(userId).collection("followers").document(currentUser.uid).delete().await()
    }

    suspend fun isFollowing(userId: String): Boolean {
        val currentUser = auth.currentUser ?: return false
        return try {
            val document = firestore.collection("users").document(currentUser.uid).collection("following").document(userId).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFollowersCount(userId: String): Int {
        return try {
            val querySnapshot = firestore.collection("users").document(userId).collection("followers").get().await()
            querySnapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getFollowingCount(userId: String): Int {
        return try {
            val querySnapshot = firestore.collection("users").document(userId).collection("following").get().await()
            querySnapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun updateUserProfile(username: String, bio: String, profileImageUrl: String?) {
        val currentUser = auth.currentUser ?: return
        val userUpdates = mutableMapOf<String, Any>()
        if (username.isNotBlank()) {
            userUpdates["username"] = username
        }
        if (bio.isNotBlank()) {
            userUpdates["bio"] = bio
        }
        if (profileImageUrl != null) {
            userUpdates["profileImageUrl"] = profileImageUrl
        }
        firestore.collection("users").document(currentUser.uid).update(userUpdates).await()
    }

    suspend fun uploadProfileImage(imageUri: Uri): String? {
        val currentUser = auth.currentUser ?: return null
        return try {
            val filename = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("profile_images/$filename")
            storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}

