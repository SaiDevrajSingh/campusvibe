package com.example.campusvibe.ui.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentFeedBinding
import com.example.campusvibe.ui.story.AddStoryActivity
import com.example.campusvibe.ui.story.StoryViewActivity

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val feedViewModel: FeedViewModel by viewModels()
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupStoriesRecyclerView()
        setupPostsRecyclerView()
        setupObservers()
        setupClickListeners()
        loadData()
    }

    private fun setupStoriesRecyclerView() {
        storyAdapter = StoryAdapter(
            onStoryClick = { story ->
                val intent = Intent(requireContext(), StoryViewActivity::class.java).apply {
                    putExtra("IMAGE_URL", story.imageUrl)
                    putExtra("USERNAME", story.username)
                }
                startActivity(intent)
            },
            onAddStoryClick = {
                val intent = Intent(requireContext(), AddStoryActivity::class.java)
                startActivity(intent)
            }
        )

        binding.storiesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = storyAdapter
        }
    }

    private fun setupPostsRecyclerView() {
        feedAdapter = FeedAdapter()

        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
        }
    }

    private fun setupObservers() {
        feedViewModel.posts.observe(viewLifecycleOwner) { posts ->
            feedAdapter.submitList(posts)
            binding.emptyTextView.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
            binding.errorTextView.visibility = View.GONE
        }

        feedViewModel.stories.observe(viewLifecycleOwner) { stories ->
            storyAdapter.submitList(stories)
        }

        feedViewModel.showError.observe(viewLifecycleOwner) {
            binding.errorTextView.visibility = View.VISIBLE
            binding.emptyTextView.visibility = View.GONE
            binding.postsRecyclerView.visibility = View.GONE
        }

        feedViewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }

    private fun setupClickListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            feedViewModel.refreshData()
        }
    }

    private fun loadData() {
        feedViewModel.loadInitialData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
