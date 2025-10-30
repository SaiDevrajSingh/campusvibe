package com.example.campusvibe.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.MessageActivity
import com.example.campusvibe.Models.Conversation
import com.example.campusvibe.R

class ConversationAdapter(private val context: Context, private val conversations: List<Conversation>) :
    RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.conversation_name)
        private val lastMessageTextView: TextView = itemView.findViewById(R.id.last_message)

        fun bind(conversation: Conversation) {
            nameTextView.text = conversation.id
            lastMessageTextView.text = conversation.lastMessage

            itemView.setOnClickListener {
                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("conversationId", conversation.id)
                context.startActivity(intent)
            }
        }
    }
}