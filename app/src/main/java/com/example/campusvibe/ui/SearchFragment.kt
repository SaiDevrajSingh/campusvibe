package com.example.campusvibe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.campusvibe.databinding.FragmentSearchBinding
import com.example.campusvibe.model.Post
import com.example.campusvibe.ui.search.SearchViewModel
import com.example.campusvibe.ui.search.PostGridAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var postAdapter: PostGridAdapter
    private var allPosts = listOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        loadData()
    }

    private fun setupRecyclerView() {
        postAdapter = PostGridAdapter(emptyList())
        binding.recyclerViewExplore.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerViewExplore.adapter = postAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })
    }

    private fun loadData() {
        viewModel.loadPosts()
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            allPosts = posts
            postAdapter.updatePosts(posts)
        }
    }

    private fun performSearch(query: String) {
        val filteredPosts = if (query.isEmpty()) {
            allPosts
        } else {
            allPosts.filter {
                it.caption.contains(query, ignoreCase = true) ||
                it.username.contains(query, ignoreCase = true)
            }
        }
        postAdapter.updatePosts(filteredPosts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
