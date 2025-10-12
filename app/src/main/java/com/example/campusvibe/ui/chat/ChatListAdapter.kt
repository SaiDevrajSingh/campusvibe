package com.example.campusvibe.ui.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

class ChatListAdapter(
    private val currentUid: String,
    private val onClick: (chatId: String, otherUid: String) -> Unit
) : ListAdapter<DocumentSnapshot, ChatListAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<DocumentSnapshot>() {
            override fun areItemsTheSame(old: DocumentSnapshot, newItem: DocumentSnapshot) = old.id == newItem.id
            @SuppressLint("DiffUtilEquals")
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

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val doc = getItem(position)
        val chatId = doc.id
        val participants = doc.get("participants") as? List<String>
        val otherUid = participants?.find { it != currentUid } ?: ""

        // You would fetch user details here based on otherUid
        holder.tvName.text = otherUid // Placeholder

        val last = doc.get("lastMessage") as? Map<String, Any>
        holder.tvLast.text = last?.get("text") as? String ?: ""

        // You would format the timestamp here
        holder.tvTime.text = ""

        // You would load the user's avatar here
        Glide.with(holder.itemView.context).load("").placeholder(R.drawable.ic_profile).into(holder.ivAvatar)

        holder.itemView.setOnClickListener {
            onClick(chatId, otherUid)
        }
    }
}
