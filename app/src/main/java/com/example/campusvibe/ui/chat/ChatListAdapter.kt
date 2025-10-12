package com.example.campusvibe.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ItemMessageReceivedBinding
import com.example.campusvibe.databinding.ItemMessageSentBinding
import com.example.campusvibe.model.Message
import com.google.firebase.auth.FirebaseAuth
class ChatListAdapter(
    private val onClick: (chatId: String, otherUid: String) -> Unit
) : ListAdapter<DocumentSnapshot, ChatListAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<DocumentSnapshot>() {
            override fun areItemsTheSame(old: DocumentSnapshot, newItem: DocumentSnapshot) = old.id == newItem.id
            override fun areContentsTheSame(old: DocumentSnapshot, newItem: DocumentSnapshot) = old.data == newItem.data
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvLast: TextView = view.findViewById(R.id.tvLast)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_chat_row, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val doc = getItem(position)
        val chatId = doc.id
        val last = doc.get("lastMessage") as? Map<*, *>
        val participants = doc.get("participants") as? List<*>
        // find the other uid (client must compute active uid)
        // assume you set a tag on adapter with currentUid or supply it in constructor
        // simplified here - replace with correct value retrieval
    }
}
