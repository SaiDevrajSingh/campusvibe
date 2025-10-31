package com.example.campusvibe.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.campusvibe.EditProfileActivity
import com.example.campusvibe.Models.User
import com.example.campusvibe.SignUpActivity
import com.example.campusvibe.adapter.ViewPagerAdapter
import com.example.campusvibe.databinding.FragmentProfileBinding
import com.example.campusvibe.utils.SupabaseClient
import com.google.android.material.tabs.TabLayoutMediator
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.editProfile.setOnClickListener{
            val intent = Intent(activity, EditProfileActivity::class.java)
            activity?.startActivity(intent)
        }
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.addFragments(MyPostFragment(),"POST")
        viewPagerAdapter.addFragments(MyReelsFragment(),"REELS")
        binding.viewPager.adapter=viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = viewPagerAdapter.getPageTitle(position)
        }.attach()

        return binding.root
    }

    companion object {

    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            fetchUserProfile()
        }
    }

    private suspend fun fetchUserProfile() {
        try {
            val supabase = SupabaseClient.client
            val currentUserId = supabase.auth.currentUserOrNull()?.id

            if (currentUserId != null) {
                // Fetch user data
                val userResponse = supabase.postgrest["users"].select {
                    filter {
                        eq("id", currentUserId)
                    }
                }.decodeSingle<User>()

                binding.name.text = userResponse.name
                binding.bio.text = userResponse.email
                if (!userResponse.image.isNullOrEmpty()) {
                    Glide.with(this).load(userResponse.image).into(binding.profileImage)
                }
                // Fetch follower count
                val followerCount = supabase.postgrest["follows"].select(head = true) {
                    filter {
                        eq("following_id", currentUserId)
                    }
                }.countOrNull()
                binding.followerCount.text = followerCount.toString()
                
                // Fetch following count
                val followingCount = supabase.postgrest["follows"].select(head = true) {
                    filter {
                        eq("follower_id", currentUserId)
                    }
                }.countOrNull()
                binding.followingCount.text = followingCount.toString()

                // Fetch post count
                val postCount = supabase.postgrest["posts"].select(head = true) {
                    filter {
                        eq("userId", currentUserId)
                    }
                }.countOrNull()
                binding.postCount.text = postCount.toString()

            }
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

}