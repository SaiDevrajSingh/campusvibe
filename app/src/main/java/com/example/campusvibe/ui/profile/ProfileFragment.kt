package com.example.campusvibe.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentProfileBinding
import com.example.campusvibe.model.User
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var postsAdapter: ProfilePostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadProfileData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        postsAdapter = ProfilePostAdapter(emptyList()) { postId ->
            // TODO: Navigate to post detail
        }
        binding.postsRecyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.postsRecyclerView.adapter = postsAdapter
    }

    private fun loadProfileData() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModel.loadUserProfile(currentUserId)
        viewModel.loadUserPosts(currentUserId)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let { displayUserProfile(it) }
        }

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            postsAdapter.updatePosts(posts.map { it.imageUrl })
        }
    }

    private fun displayUserProfile(user: User) {
        binding.usernameTextView.text = user.username
        binding.fullNameTextView.text = user.fullName
        binding.bioTextView.text = user.bio.takeIf { it.isNotEmpty() } ?: "No bio yet"
        binding.followersCountTextView.text = "${user.followers.size} followers"
        binding.followingCountTextView.text = "${user.following.size} following"
        binding.postsCountTextView.text = "${user.postsCount} posts"

        Glide.with(this)
            .load(user.profileImageUrl.takeIf { it.isNotEmpty() } ?: R.drawable.ic_profile)
            .circleCrop()
            .into(binding.profileImageView)
    }

    private fun setupClickListeners() {
        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        binding.followersLayout.setOnClickListener {
            // TODO: Navigate to followers list
        }

        binding.followingLayout.setOnClickListener {
            // TODO: Navigate to following list
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


