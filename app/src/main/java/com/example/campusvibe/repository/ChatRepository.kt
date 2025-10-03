package com.example.campusvibe.repository

import android.net.Uri
import com.example.campusvibe.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
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
    }

    suspend fun sendMessage(conversationId: String, text: String) {
        val userId = auth.currentUser?.uid ?: return
        val message = Message(senderId = userId, text = text, timestamp = System.currentTimeMillis())
        firestore.collection("conversations").document(conversationId)
            .collection("messages").add(message).await()
    }

    suspend fun sendMediaMessage(conversationId: String, mediaUri: Uri, mediaType: String, senderId: String) {
        val timestamp = System.currentTimeMillis()
        val mediaUrl = uploadMediaToStorage(mediaUri, mediaType, timestamp)

        val message = Message(
            id = "",
            senderId = senderId,
            text = "",
            mediaUrl = mediaUrl,
            mediaType = mediaType,
            timestamp = timestamp
        )

        firestore.collection("conversations").document(conversationId)
            .collection("messages").add(message).await()
    }

    private suspend fun uploadMediaToStorage(mediaUri: Uri, mediaType: String, timestamp: Long): String {
        val storage = FirebaseStorage.getInstance()
        val extension = if (mediaType.startsWith("image")) "jpg" else "mp4"
        val fileName = "chat_media_${timestamp}.$extension"

        val ref = storage.reference.child("chat_media/$fileName")
        val uploadTask = ref.putFile(mediaUri).await()
        return uploadTask.storage.downloadUrl.await().toString()
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
                val typingUsers = snapshot?.get("typingUsers") as? List<*> ?: emptyList<String>()
                trySend(typingUsers.filterIsInstance<String>()) 
            }
        awaitClose { listener.remove() }
    }
}
