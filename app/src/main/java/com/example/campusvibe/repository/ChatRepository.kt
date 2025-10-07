package com.example.campusvibe.repository

import android.net.Uri
import com.example.campusvibe.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

    suspend fun sendMessage(conversationId: String, messageText: String) {
        val message = Message(
            senderId = "your_sender_id", // Replace with actual sender ID
            text = messageText
        )
        firestore.collection("conversations").document(conversationId).collection("messages").add(message).await()
    }

    suspend fun sendMediaMessage(conversationId: String, mediaUri: Uri, mediaType: String, senderId: String) {
        val mediaRef = storage.reference.child("media/${UUID.randomUUID()}")
        mediaRef.putFile(mediaUri).await()
        val mediaUrl = mediaRef.downloadUrl.await().toString()

        val message = Message(
            senderId = senderId,
            mediaUrl = mediaUrl,
            mediaType = mediaType
        )
        firestore.collection("conversations").document(conversationId).collection("messages").add(message).await()
    }
}
