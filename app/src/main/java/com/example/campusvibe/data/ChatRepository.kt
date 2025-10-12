package com.example.campusvibe.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun observeMessages(chatId: String): Flow<QuerySnapshot> {
        return firestore.collection("chats").document(chatId)
            .collection("messages").orderBy("timestamp", Query.Direction.ASCENDING)
            .snapshots()
    }

    suspend fun sendMessage(chatId: String, message: Map<String, Any>) {
        // Create chat document if it doesn't exist
        firestore.collection("chats").document(chatId).get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val participants = chatId.split("-")
                firestore.collection("chats").document(chatId).set(mapOf("participants" to participants))
            }
        }

        firestore.collection("chats").document(chatId)
            .collection("messages").add(message).await()
        firestore.collection("chats").document(chatId).update("lastMessage", message).await()
    }

    suspend fun markChatAsRead(chatId: String, myUid: String) {
        // Implementation for marking messages as read
    }

    fun observeChatsFor(uid: String): Flow<QuerySnapshot> {
        return firestore.collection("chats")
            .whereArrayContains("participants", uid)
            .snapshots()
    }

    suspend fun searchUsers(query: String): List<Pair<String, Map<String, Any>>> {
        val users = mutableListOf<Pair<String, Map<String, Any>>>()
        if (query.isEmpty()) {
            return users
        }
        val result = firestore.collection("users")
            .whereGreaterThanOrEqualTo("username", query)
            .whereLessThanOrEqualTo("username", query + '\uf8ff')
            .get().await()
        for (document in result.documents) {
            document.data?.let {
                users.add(Pair(document.id, it))
            }
        }
        return users
    }
}