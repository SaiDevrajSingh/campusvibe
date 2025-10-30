package com.example.campusvibe.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.Models.User
import com.example.campusvibe.SignUpActivity
import com.example.campusvibe.adapter.ViewPagerAdapter
import com.example.campusvibe.databinding.FragmentProfileBinding
import com.example.campusvibe.utils.SupabaseClient
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
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
            val intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra("MODE",1)
            activity?.startActivity(intent)
            activity?.finish()
        }
        viewPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPagerAdapter.addFragments(MyPostFragment(),"POST")
        viewPagerAdapter.addFragments(MyReelsFragment(),"REELS")
        binding.viewPager.adapter=viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "POST"
                1 -> tab.text = "REELS"
            }
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
                    filter("uid", io.github.jan.supabase.postgrest.query.FilterOperator.EQ, currentUserId) 
                }.decodeSingle<User>()

                binding.name.text = userResponse.name
                binding.bio.text = userResponse.email
                if (!userResponse.image.isNullOrEmpty()) {
                    Picasso.get().load(userResponse.image).into(binding.profileImage)
                }
                binding.followerCount.text = userResponse.followers.size.toString()
                binding.followingCount.text = userResponse.following.size.toString()

                // Fetch post count
                val postCountResponse = supabase.postgrest["posts"].select("count") { 
                    filter("uid", io.github.jan.supabase.postgrest.query.FilterOperator.EQ, currentUserId) 
                }
                binding.postCount.text = postCountResponse.data

            }
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

}