package com.example.campusvibe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import java.text.SimpleDateFormat
import java.util.*
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.Models.Post
import com.example.campusvibe.Models.Story
import com.example.campusvibe.Models.User
import com.example.campusvibe.R
import com.example.campusvibe.adapter.PostAdapter
import com.example.campusvibe.adapter.StoryAdapter
import com.example.campusvibe.databinding.FragmentHomeBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Follow(val follower_id: String, val following_id: String)

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var postList = ArrayList<Post>()
    private lateinit var adapter: PostAdapter
    private var storyList = ArrayList<Story>()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        
        // Get current user ID from Supabase Auth
        val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""
        adapter = PostAdapter(requireContext(), postList, currentUserId)
        
        // Initialize story adapter
        storyAdapter = StoryAdapter(requireContext(), storyList)
        binding.storyRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = storyAdapter
            setHasFixedSize(true)
        }
        
        binding.postRv.layoutManager = LinearLayoutManager(requireContext())
        binding.postRv.adapter = adapter

        fetchData()

        return binding.root
    }

    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val supabase = SupabaseClient.client
                val currentUser = supabase.auth.currentUserOrNull()
                
                Log.d("HomeFragment", "Current user: ${currentUser?.id}")

                if (currentUser != null) {
                    // Fetch Posts from followed users
                    val followedUserIds = supabase.postgrest["follows"].select { 
                        filter {
                            eq("follower_id", currentUser.id)
                        }
                    }.decodeList<Follow>().map { it.following_id }

                    // Add current user's ID to see their own posts and stories
                    val allUserIds = followedUserIds + currentUser.id
                    
                    // Fetch posts
                    val posts = supabase.postgrest["posts"].select {
                        filter {
                            isIn("user_id", allUserIds)
                        }
                    }.decodeList<Post>()
                    
                    Log.d("HomeFragment", "Fetched ${posts.size} posts for users: $allUserIds")
                    
                    postList.clear()
                    postList.addAll(posts)
                    adapter.notifyDataSetChanged()

                    // Fetch Stories from the last 24 hours from followed users and current user
                    val oneDayAgoMs = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                    
                    Log.d("StoryDebug", "Fetching stories from database for users: $allUserIds")
                    val allStories = try {
                        supabase.postgrest["stories"]
                            .select() {
                                filter {
                                    isIn("user_id", allUserIds)
                                }
                            }
                            .decodeList<Story>()
                    } catch (e: Exception) {
                        Log.e("StoryDebug", "Error fetching stories: ${e.message}")
                        emptyList()
                    }
                    
                    Log.d("StoryDebug", "Total stories in DB for followed users: ${allStories.size}")
                    
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    
                    val stories = allStories.filter { story -> 
                        try {
                            val storyDate = dateFormat.parse(story.timestamp) ?: return@filter false
                            val storyTime = storyDate.time
                            val isRecent = storyTime >= oneDayAgoMs
                            isRecent
                        } catch (e: Exception) {
                            Log.e("StoryDebug", "Error processing story ${story.id}: ${e.message}")
                            false
                        }
                    }
                    
                    // Get unique user IDs from stories
                    val userIds = stories.map { it.userId }.distinct()
                    
                    if (userIds.isNotEmpty()) {
                        // Fetch user data for stories
                        val users = try {
                            supabase.postgrest["users"]
                                .select()
                                .decodeList<User>()
                                .filter { it.id in userIds }
                                .associateBy { it.id }
                        } catch (e: Exception) {
                            Log.e("StoryDebug", "Error fetching user data: ${e.message}")
                            emptyMap()
                        }
                        
                        // Merge story and user data
                        val storiesWithUsers = stories.map { story ->
                            val userData = users[story.userId]
                            story.copy(
                                username = userData?.username ?: "Unknown",
                                profileImage = userData?.image
                            )
                        }
                        
                        Log.d("StoryDebug", "Found ${storiesWithUsers.size} recent stories with user data")
                        storyList.clear()
                        storyList.addAll(storiesWithUsers)
                        storyAdapter.notifyDataSetChanged()
                    } else {
                        // Clear stories if there are none
                        storyList.clear()
                        storyAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
