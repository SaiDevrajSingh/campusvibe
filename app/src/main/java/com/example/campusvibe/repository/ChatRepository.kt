package com.example.campusvibe.repository

import com.example.campusvibe.data.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        if (conversationId.isEmpty()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val collection = firestore.collection("conversations").document(conversationId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val registration = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val messages = snapshot.toObjects(Message::class.java)
                trySend(messages)
            }
        }
        awaitClose { registration.remove() }
    }
}
