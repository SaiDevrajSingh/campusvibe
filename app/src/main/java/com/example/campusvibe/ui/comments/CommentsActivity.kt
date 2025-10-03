package com.example.campusvibe.ui.comments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.ActivityCommentsBinding
import com.example.campusvibe.model.Comment

class CommentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getStringExtra("postId") ?: ""
        val postImageUrl = intent.getStringExtra("postImageUrl") ?: ""
        val postCaption = intent.getStringExtra("postCaption") ?: ""

        setupRecyclerView()

        // Load comments for the post
        loadComments(postId)
    }

    private fun setupRecyclerView() {
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CommentsAdapter(emptyList())
        binding.commentsRecyclerView.adapter = adapter
    }

    private fun loadComments(postId: String) {
        // TODO: Implement loading comments for the specific post
        // For now, show sample comments
        val sampleComments = listOf(
            Comment(username = "user1", text = "Great post!", postId = postId),
            Comment(username = "user2", text = "Love this!", postId = postId),
            Comment(username = "user3", text = "Amazing content!", postId = postId)
        )
        (binding.commentsRecyclerView.adapter as? CommentsAdapter)?.updateComments(sampleComments)
    }
}
