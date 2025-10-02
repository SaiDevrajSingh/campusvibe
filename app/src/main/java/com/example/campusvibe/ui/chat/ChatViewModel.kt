package com.example.campusvibe.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.campusvibe.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class ChatViewModel(private val conversationId: String) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    init {
        listenForMessages()
    }

    private fun listenForMessages() {
        FirebaseDatabase.getInstance().getReference("messages").child(conversationId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<Message>()
                    for (child in snapshot.children) {
                        val message = child.getValue(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    _messages.value = messageList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun sendMessage(text: String, senderId: String) {
        val message = Message(
            senderId = senderId,
            text = text,
            timestamp = Date(System.currentTimeMillis())
        )
        FirebaseDatabase.getInstance().getReference("messages").child(conversationId)
            .push()
            .setValue(message)
    }
}

