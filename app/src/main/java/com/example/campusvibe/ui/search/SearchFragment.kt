package com.example.campusvibe.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.campusvibe.databinding.FragmentSearchBinding
import com.example.campusvibe.model.Post

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView with a GridLayoutManager
        binding.recyclerViewExplore.layoutManager = GridLayoutManager(context, 3)

        // Create and set the adapter for the RecyclerView
        val posts = emptyList<Post>() // We'll populate this later
        binding.recyclerViewExplore.adapter = PostGridAdapter(posts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


