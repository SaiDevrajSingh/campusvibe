package com.example.campusvibe.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

        viewModel.loadProfile(currentUserId)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let { displayUserProfile(it) }
        }

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            postsAdapter.updatePosts(posts.map { it.imageUrl })
        }
    }

    private fun displayUserProfile(user: User) {
        // The layout uses different IDs, so we need to find views by ID
        val fullnameTextView = view?.findViewById<TextView>(R.id.fullname)
        val bioTextView = view?.findViewById<TextView>(R.id.bio)
        val postsCountTextView = view?.findViewById<TextView>(R.id.posts_count)
        val followersCountTextView = view?.findViewById<TextView>(R.id.followers_count)
        val followingCountTextView = view?.findViewById<TextView>(R.id.following_count)
        val profileImageView = view?.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profile_image)

        fullnameTextView?.text = user.fullName
        bioTextView?.text = user.bio ?: "No bio yet"
        postsCountTextView?.text = user.postsCount.toString()
        followersCountTextView?.text = user.followers.size.toString()
        followingCountTextView?.text = user.following.size.toString()

        profileImageView?.let { imageView ->
            Glide.with(this)
                .load(user.profileImageUrl ?: R.drawable.ic_profile)
                .circleCrop()
                .into(imageView)
        }
    }

    private fun setupClickListeners() {
        val editProfileButton = view?.findViewById<Button>(R.id.edit_profile_button)
        editProfileButton?.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


