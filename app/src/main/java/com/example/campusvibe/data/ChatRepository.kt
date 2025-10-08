package com.example.campusvibe.data

import android.net.Uri
import com.example.campusvibe.model.Conversation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun sendMessage(conversationId: String, message: Message): Boolean {
        return try {
            firestore.collection("conversations").document(conversationId)
                .collection("messages").add(message).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("conversations").document(conversationId)
            .collection("messages").orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages).isSuccess
            }
        awaitClose { listener.remove() }
    }

    fun getConversations(userId: String): Flow<List<Conversation>> = callbackFlow {
        val listener = firestore.collection("conversations")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val conversations = snapshot?.documents?.mapNotNull { document ->
                    val conversation = document.toObject(Conversation::class.java)
                    conversation?.id = document.id
                    conversation
                } ?: emptyList()
                trySend(conversations).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun getConversation(userId1: String, userId2: String): Conversation? {
        val querySnapshot = firestore.collection("conversations")
            .whereArrayContains("participants", userId1)
            .get()
            .await()

        for (document in querySnapshot.documents) {
            val conversation = document.toObject(Conversation::class.java)
            if (conversation?.participants?.contains(userId2) == true) {
                conversation.id = document.id
                return conversation
            }
        }
        return null
    }

    suspend fun sendMediaMessage(conversationId: String, mediaUri: Uri, mediaType: String, senderId: String): Boolean {
        return try {
            // For now, create a message with media URL as placeholder
            // In a real implementation, you would upload the media file first
            val message = Message(
                senderId = senderId,
                mediaType = mediaType,
                timestamp = System.currentTimeMillis()
            )
            firestore.collection("conversations").document(conversationId)
                .collection("messages").add(message).await()
            true
        } catch (e: Exception) {
            false
        }
    }


