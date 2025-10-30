package com.example.campusvibe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.Models.Message
import com.example.campusvibe.adapter.MessageAdapter
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.PostgresChangeFilter
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class MessageActivity : AppCompatActivity() {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button

    private val supabase by lazy { SupabaseClient.client }

    private var conversationId: String? = null
    private var channel: RealtimeChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        conversationId = intent.getStringExtra("conversationId")

        messagesRecyclerView = findViewById(R.id.messages_recycler_view)
        messageInput = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)

        messageAdapter = MessageAdapter(this, messages)
        messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MessageActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }

        sendButton.setOnClickListener {
            lifecycleScope.launch {
                sendMessage()
            }
        }

        lifecycleScope.launch {
            conversationId?.let {
                fetchMessages(it)
                subscribeToMessages(it)
            }
        }
    }

    private suspend fun fetchMessages(conversationId: String) {
        try {
            val response = supabase.postgrest["messages"]
                .select {
                    filter {
                        eq("conversation_id", conversationId)
                    }
                    order("timestamp", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
            val fetchedMessages = response.decodeList<Message>()
            messages.clear()
            messages.addAll(fetchedMessages)
            messageAdapter.notifyDataSetChanged()
            messagesRecyclerView.scrollToPosition(messages.size - 1)
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun subscribeToMessages(conversationId: String) {
        channel = supabase.channel("messages-$conversationId")
        lifecycleScope.launch {
            channel?.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                table = "messages"
                filter = "\"conversation_id\", \"eq\", \"$conversationId\""
            }?.collect { 
                val newMessage = Json.decodeFromJsonElement<Message>(it.record)
                messages.add(newMessage)
                runOnUiThread {
                    messageAdapter.notifyItemInserted(messages.size - 1)
                    messagesRecyclerView.scrollToPosition(messages.size - 1)
                }
            }
        }
        lifecycleScope.launch {
            channel?.subscribe()
        }
    }

    private suspend fun sendMessage() {
        val text = messageInput.text.toString().trim()
        if (text.isEmpty()) return

        val userId = supabase.auth.currentUserOrNull()?.id ?: return
        val convId = this.conversationId ?: return

        val message = Message(
            senderId = userId,
            text = text,
            timestamp = System.currentTimeMillis().toString(),
            conversationId = convId
        )

        messageInput.text.clear()

        try {
            // Insert the new message
            supabase.postgrest["messages"].insert(message)

            // Update the conversation's last message and timestamp
            val conversationUpdate = mapOf(
                "last_message" to text,
                "last_message_timestamp" to System.currentTimeMillis().toString()
            )
            supabase.postgrest["conversations"].update(conversationUpdate) {
                filter {
                    eq("id", convId)
                }
            }
        } catch (e: Exception) {
            // Handle error, maybe show a toast
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            channel?.unsubscribe()
        }
    }
}
