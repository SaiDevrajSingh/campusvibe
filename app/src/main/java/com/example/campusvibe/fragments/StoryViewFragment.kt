package com.example.campusvibe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentStoryViewBinding

class StoryViewFragment : Fragment() {
    private lateinit var binding: FragmentStoryViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryViewBinding.inflate(inflater, container, false)
        
        val imageUrl = arguments?.getString("imageUrl")
        val username = arguments?.getString("username")
        val profileImageUrl = arguments?.getString("profileImageUrl")
        
        Glide.with(this).load(imageUrl).into(binding.storyImageView)
        binding.storyUsername.text = username
        
        if (profileImageUrl != null) {
            Glide.with(this).load(profileImageUrl).into(binding.storyProfileImage)
        } else {
            binding.storyProfileImage.setImageResource(R.drawable.user)
        }

        return binding.root
    }
}
