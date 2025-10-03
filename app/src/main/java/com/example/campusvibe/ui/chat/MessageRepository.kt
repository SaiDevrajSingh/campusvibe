package com.example.campusvibe.ui.chat

import com.example.campusvibe.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getMessages(conversationId: String): Flow<List<Message>> {
        return firestore.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects<Message>()
            }
    }

    fun sendMessage(conversationId: String, message: Message) {
        firestore.collection("conversations").document(conversationId)
            .collection("messages")
            .add(message)
    }
}
