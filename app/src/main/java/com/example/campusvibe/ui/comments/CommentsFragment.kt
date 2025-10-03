package com.example.campusvibe.ui.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentCommentsBinding

class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val comments = listOf(
            Comment(username = "user1", text = "This is the first comment"),
            Comment(username = "user2", text = "This is another comment")
        )

        val adapter = CommentsAdapter(comments)
        binding.commentsRecyclerView.adapter = adapter
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.postCommentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                // For now, just show a toast
                Toast.makeText(context, "Comment posted: $commentText", Toast.LENGTH_SHORT).show()
                binding.commentEditText.text.clear()
            } else {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

