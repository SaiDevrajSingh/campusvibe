package com.example.campusvibe.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.EditProfileActivity
import com.example.campusvibe.adapter.ViewPagerAdapter
import com.example.campusvibe.databinding.FragmentProfileBinding
import com.example.campusvibe.utils.SupabaseClient
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.addFragments(MyPostFragment(), "My Post")
        viewPagerAdapter.addFragments(MyReelsFragment(), "My Reels")
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = viewPagerAdapter.getPageTitle(position)
        }.attach()

        binding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val currentUser = SupabaseClient.client.auth.currentUserOrNull()

        if (currentUser != null) {
            val userId = currentUser.id
            lifecycleScope.launch {
                try {
                    val user = SupabaseClient.client.postgrest.from("users").select() { filter {
                        eq("id", userId)
                    } }.data

                    val jsonObject = Json.parseToJsonElement(user).jsonObject
                    val name = jsonObject["name"]?.jsonPrimitive?.content
                    val bio = jsonObject["bio"]?.jsonPrimitive?.content
                    val imageUrl = jsonObject["image"]?.jsonPrimitive?.content
                    val followers = jsonObject["followers"]?.jsonPrimitive?.content
                    val following = jsonObject["following"]?.jsonPrimitive?.content

                    binding.name.text = name
                    binding.bio.text = bio
                    if (imageUrl != null) {
                        Picasso.get().load(imageUrl).into(binding.profileImage)
                    }
                    binding.followerCount.text = followers
                    binding.followingCount.text = following

                    val postResult = SupabaseClient.client.postgrest.from("posts").select() { filter {
                        eq("user_id", userId)
                    } }.data
                    val postCount = Json.parseToJsonElement(postResult).jsonObject.size
                    binding.postCount.text = postCount.toString()

                } catch (e: Exception) {
                    // Handle exception
                }
            }
        }
    }
}