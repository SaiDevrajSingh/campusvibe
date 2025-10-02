package com.example.campusvibe.repository

import com.example.campusvibe.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(conversationId: String, text: String) {
        val userId = auth.currentUser?.uid ?: return
        val message = Message(senderId = userId, text = text, timestamp = Date(System.currentTimeMillis()))
        firestore.collection("conversations").document(conversationId)
            .collection("messages")
            .add(message)
            .await()
    }

    fun setTypingIndicator(conversationId: String, isTyping: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("conversations").document(conversationId)
            .update("typingUsers", if (isTyping) listOf(userId) else emptyList())
    }

    fun getTypingIndicator(conversationId: String): Flow<List<String>> = callbackFlow {
        val listener = firestore.collection("conversations").document(conversationId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val typingUsers = snapshot?.get("typingUsers") as? List<String> ?: emptyList()
                trySend(typingUsers)
            }
        awaitClose { listener.remove() }
    }
}

