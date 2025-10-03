package com.example.campusvibe.ui.comments

import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.databinding.ItemCommentBinding
import com.example.campusvibe.model.Comment

class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: Comment) {
        binding.usernameTextView.text = comment.username
        binding.commentTextView.text = comment.text
    }
}
