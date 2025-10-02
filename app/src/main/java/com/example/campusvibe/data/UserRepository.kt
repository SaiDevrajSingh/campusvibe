package com.example.campusvibe.data

import com.example.campusvibe.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()

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

    suspend fun getUser(userId: String): User? {
        return try {
            firestore.collection("users").document(userId).get().await()
                .toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}


