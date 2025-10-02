package com.example.campusvibe.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentFeedBinding
import com.example.campusvibe.model.Post

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var feedAdapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val posts = createDummyPosts()
        feedAdapter = FeedAdapter()
        feedAdapter.submitList(posts)

        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
        }
    }

    private fun createDummyPosts(): MutableList<Post> {
        return mutableListOf(
            Post(
                id = "1",
                userId = "user1",
                username = "user1",
                imageUrl = "https://picsum.photos/400",
                caption = "This is a great photo!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                likes = 10,
                isLiked = false
            ),
            Post(
                id = "2",
                userId = "user2",
                username = "user2",
                imageUrl = "https://picsum.photos/401",
                caption = "Having fun!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                likes = 25,
                isLiked = true
            ),
            Post(
                id = "3",
                userId = "user3",
                username = "user3",
                imageUrl = "https://picsum.photos/402",
                caption = "Beautiful scenery.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48,
                likes = 50,
                isLiked = false
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


