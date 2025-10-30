package com.example.campusvibe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.Models.Post
import com.example.campusvibe.Models.Story
import com.example.campusvibe.Models.User
import com.example.campusvibe.R
import com.example.campusvibe.adapter.FollowAdapter
import com.example.campusvibe.adapter.PostAdapter
import com.example.campusvibe.adapter.StoryAdapter
import com.example.campusvibe.databinding.FragmentHomeBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var postList = ArrayList<Post>()
    private lateinit var adapter: PostAdapter
    private var followList = ArrayList<User>()
    private lateinit var followAdapter: FollowAdapter
    private var storyList = ArrayList<Story>()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = PostAdapter(requireContext(), postList)
        binding.postRv.layoutManager = LinearLayoutManager(requireContext())
        binding.postRv.adapter = adapter

        followAdapter = FollowAdapter(requireContext(), followList)
        binding.followRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.followRv.adapter = followAdapter

        storyAdapter = StoryAdapter(requireContext(), storyList)
        binding.storyRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.storyRv.adapter = storyAdapter

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.option_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.like -> {
                        findNavController().navigate(R.id.notification)
                        return true
                    }
                    R.id.message -> {
                        findNavController().navigate(R.id.message)
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        fetchData()

        return binding.root
    }

    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val supabase = SupabaseClient.client

                // Fetch Posts
                val postResponse = supabase.postgrest["posts"].select()
                val posts = postResponse.decodeList<Post>()
                postList.clear()
                postList.addAll(posts)
                adapter.notifyDataSetChanged()

                // Fetch Stories
                val storyResponse = supabase.postgrest["stories"].select()
                val stories = storyResponse.decodeList<Story>()
                storyList.clear()
                storyList.addAll(stories)
                storyAdapter.notifyDataSetChanged()

                // Fetch Followed Users
                val currentUserId = supabase.auth.currentUserOrNull()?.id
                if (currentUserId != null) {
                    val followedUsersResponse = supabase.postgrest["users"].select {
                        filter("followers.follower_id", "eq.$currentUserId")
                    }
                    val followedUsers = followedUsersResponse.decodeList<User>()
                    followList.clear()
                    followList.addAll(followedUsers)
                    followAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

}