package com.example.campusvibe.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentSearchBinding
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.User
import com.google.android.material.tabs.TabLayout

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var postAdapter: PostGridAdapter
    private lateinit var userAdapter: UserSearchAdapter
    private var allPosts = listOf<Post>()
    private var allUsers = listOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearchView()
        setupTabs()
        loadData()
    }

    private fun setupRecyclerViews() {
        // Posts grid
        postAdapter = PostGridAdapter(emptyList())
        binding.recyclerViewExplore.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerViewExplore.adapter = postAdapter

        // Users list
        userAdapter = UserSearchAdapter(emptyList()) { user ->
            // Navigate to user profile
            val intent = android.content.Intent(requireContext(), androidx.fragment.app.FragmentActivity::class.java)
            // For now, navigate to profile fragment with user ID
            // TODO: Create proper UserProfileActivity or enhance ProfileFragment to show other users
            android.widget.Toast.makeText(requireContext(), "Opening ${user.username}'s profile", android.widget.Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewUsers.adapter = userAdapter
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

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Posts"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Users"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showPostsTab()
                    1 -> showUsersTab()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Show posts tab by default
        showPostsTab()
    }

    private fun showPostsTab() {
        binding.recyclerViewExplore.visibility = View.VISIBLE
        binding.recyclerViewUsers.visibility = View.GONE
    }

    private fun showUsersTab() {
        binding.recyclerViewExplore.visibility = View.GONE
        binding.recyclerViewUsers.visibility = View.VISIBLE
    }

    private fun loadData() {
        viewModel.loadPosts()
        viewModel.loadUsers()

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            allPosts = posts
            postAdapter.updatePosts(posts)
        }

        viewModel.users.observe(viewLifecycleOwner) { users ->
            allUsers = users
            userAdapter.updateUsers(users)
        }
    }

    private fun performSearch(query: String) {
        if (binding.tabLayout.selectedTabPosition == 0) {
            // Search posts
            val filteredPosts = if (query.isEmpty()) {
                allPosts
            } else {
                allPosts.filter {
                    it.caption.contains(query, ignoreCase = true) ||
                    it.username.contains(query, ignoreCase = true)
                }
            }
            postAdapter.updatePosts(filteredPosts)
        } else {
            // Search users
            val filteredUsers = if (query.isEmpty()) {
                allUsers
            } else {
                allUsers.filter {
                    it.username.contains(query, ignoreCase = true) ||
                    it.fullName.contains(query, ignoreCase = true)
                }
            }
            userAdapter.updateUsers(filteredUsers)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


