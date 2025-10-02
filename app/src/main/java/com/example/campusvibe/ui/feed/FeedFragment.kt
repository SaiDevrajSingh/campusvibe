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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedAdapter = FeedAdapter()

        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
        }

        feedViewModel.posts.observe(viewLifecycleOwner) {
            feedAdapter.submitList(it)
            binding.emptyTextView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.errorTextView.visibility = View.GONE
        }

        feedViewModel.showError.observe(viewLifecycleOwner) {
            binding.errorTextView.visibility = View.VISIBLE
            binding.emptyTextView.visibility = View.GONE
            binding.postsRecyclerView.visibility = View.GONE
        }

        binding.addStoryButton.setOnClickListener {
            val intent = Intent(requireContext(), AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            feedViewModel.loadPosts()
        }

        feedViewModel.loadPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
