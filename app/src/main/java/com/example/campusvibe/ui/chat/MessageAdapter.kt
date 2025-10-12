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

private const val VIEW_TYPE_SENT = 1
private const val VIEW_TYPE_RECEIVED = 2

class MessagesAdapter(private val myUid: String) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(old: ChatMessage, newItem: ChatMessage) = old.id == newItem.id
            override fun areContentsTheSame(old: ChatMessage, newItem: ChatMessage) = old == newItem
        }
        const val TYPE_SENT = 1
        const val TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).from == myUid) TYPE_SENT else TYPE_RECEIVED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = if (viewType == TYPE_SENT) R.layout.item_message_sent else R.layout.item_message_received
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = getItem(position)
        val tv = holder.itemView.findViewById<TextView>(R.id.tvMessage)
        val tvTime = holder.itemView.findViewById<TextView>(R.id.tvTime)
        tv.text = msg.text
        tvTime.text = msg.timestamp?.toDate()?.let { android.text.format.DateFormat.format("hh:mm a", it) } ?: ""
    }
}
