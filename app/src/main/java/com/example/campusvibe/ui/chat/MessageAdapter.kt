package com.example.campusvibe.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.databinding.ItemMessageBinding
import com.example.campusvibe.databinding.ItemMessageSentBinding
import com.example.campusvibe.model.Message
import com.google.firebase.auth.FirebaseAuth

private const val VIEW_TYPE_SENT = 1
private const val VIEW_TYPE_RECEIVED = 2

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.itemViewType == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).bind(message)
        } else {
            (holder as ReceivedMessageViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text_view)
        private val messageImage: ImageView? = itemView.findViewById(R.id.message_image_view)

        fun bind(message: Message) {
            if (message.text.isNotEmpty()) {
                messageText.text = message.text
                messageText.visibility = View.VISIBLE
            } else {
                messageText.visibility = View.GONE
            }

            if (!message.mediaUrl.isNullOrEmpty()) {
                messageImage?.let { imageView ->
                    imageView.visibility = View.VISIBLE
                    Glide.with(itemView.context)
                        .load(message.mediaUrl)
                        .into(imageView)
                }
            } else {
                messageImage?.visibility = View.GONE
            }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_view_message)
        private val messageImage: ImageView? = itemView.findViewById(R.id.message_image_view)

        fun bind(message: Message) {
            if (message.text.isNotEmpty()) {
                messageText.text = message.text
                messageText.visibility = View.VISIBLE
            } else {
                messageText.visibility = View.GONE
            }

            if (!message.mediaUrl.isNullOrEmpty()) {
                messageImage?.let { imageView ->
                    imageView.visibility = View.VISIBLE
                    Glide.with(itemView.context)
                        .load(message.mediaUrl)
                        .into(imageView)
                }
            } else {
                messageImage?.visibility = View.GONE
            }
        }
    }
}


