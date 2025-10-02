package com.example.campusvibe.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.model.Conversation
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class ConversationAdapter(
    private var conversations: List<Conversation>,
    private val onConversationClicked: (Conversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    fun updateConversations(newConversations: List<Conversation>) {
        conversations = newConversations
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val conversationName: TextView = itemView.findViewById(R.id.text_view_conversation_name)
        private val lastMessage: TextView = itemView.findViewById(R.id.text_view_last_message)
        private val profileImage: CircleImageView = itemView.findViewById(R.id.image_view_profile)

        fun bind(conversation: Conversation) {
            val otherUserId = conversation.participants.firstOrNull { it != FirebaseAuth.getInstance().currentUser?.uid } ?: ""
            conversationName.text = otherUserId
            lastMessage.text = conversation.lastMessage?.text
            // In a real app, you would fetch the user's details (name and profile picture) using the otherUserId
            // For now, we're just displaying the user ID and a placeholder image
            Glide.with(itemView.context).load(R.drawable.ic_profile).into(profileImage)
            itemView.setOnClickListener {
                onConversationClicked(conversation)
            }
        }
    }
}


