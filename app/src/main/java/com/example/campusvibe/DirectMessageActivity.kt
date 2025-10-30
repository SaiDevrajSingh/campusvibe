package com.example.campusvibe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.Models.Conversation
import com.example.campusvibe.adapter.ConversationAdapter
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class DirectMessageActivity : AppCompatActivity() {

    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter
    private val conversations = mutableListOf<Conversation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_message)

        conversationsRecyclerView = findViewById(R.id.conversations_recycler_view)
        conversationAdapter = ConversationAdapter(this, conversations)

        conversationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DirectMessageActivity)
            adapter = conversationAdapter
        }

        fetchConversations()
    }

    private fun fetchConversations() {
        lifecycleScope.launch {
            try {
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return@launch
                val response = SupabaseClient.client.postgrest["conversations"]
                    .select {
                        filter {
                            cs("participants", listOf(userId))
                        }
                    }
                val conversationList = response.decodeList<Conversation>()

                conversations.clear()
                conversations.addAll(conversationList)
                conversationAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
