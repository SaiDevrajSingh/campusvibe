package com.example.campusvibe.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.campusvibe.model.Conversation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConversationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getConversations(): LiveData<List<Conversation>> {
        val conversations = MutableLiveData<List<Conversation>>()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("conversations")
                .whereArrayContains("participants", userId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val conversationList = snapshot.toObjects(Conversation::class.java)
                        conversations.postValue(conversationList)
                    }
                }
        }

        return conversations
    }
}


