package com.example.campusvibe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentHomeBinding
import com.example.campusvibe.model.Post

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the action bar for this fragment
        (activity as AppCompatActivity?)?.supportActionBar?.hide()

        setupRecyclerView()
        observePosts()
        viewModel.fetchFeedPosts()

        binding.chatButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_chat_list)
        }

        binding.notificationButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_notifications)
        }
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(emptyList())
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewPosts.adapter = adapter
    }

    private fun observePosts() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts.map { it.post })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show the action bar again when the fragment is destroyed
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        _binding = null
    }
}