package com.example.campusvibe.ui.likes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusvibe.databinding.FragmentLikesBinding

class LikesFragment : Fragment() {

    private var _binding: FragmentLikesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LikesViewModel by viewModels()
    private lateinit var likesAdapter: LikesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likesAdapter = LikesAdapter(emptyList())
        binding.likesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = likesAdapter
        }

        viewModel.likedPosts.observe(viewLifecycleOwner) { posts ->
            if (posts.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.likesRecyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.likesRecyclerView.visibility = View.VISIBLE
                likesAdapter.posts = posts
                likesAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

