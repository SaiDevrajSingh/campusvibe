package com.example.campusvibe.data

import com.example.campusvibe.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun searchUsers(query: String): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .whereGreaterThanOrEqualTo("username", query)
            .whereLessThanOrEqualTo("username", query + "\uf8ff")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val users = snapshot?.toObjects(User::class.java) ?: emptyList()
                trySend(users).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun followUser(userId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(currentUserId)
            .update("following", FieldValue.arrayUnion(userId)).await()
        firestore.collection("users").document(userId)
            .update("followers", FieldValue.arrayUnion(currentUserId)).await()
    }

    suspend fun unfollowUser(userId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(currentUserId)
            .update("following", FieldValue.arrayRemove(userId)).await()
        firestore.collection("users").document(userId)
            .update("followers", FieldValue.arrayRemove(currentUserId)).await()
    }

    suspend fun isFollowing(userId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false
        val userDoc = firestore.collection("users").document(currentUserId).get().await()
        val following = userDoc.get("following") as? List<*> ?: emptyList<String>()
        return following.contains(userId)
    }

    suspend fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users").addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val users = snapshot?.toObjects(User::class.java) ?: emptyList()
            trySend(users)
        }
        awaitClose { listener.remove() }
    }
}


