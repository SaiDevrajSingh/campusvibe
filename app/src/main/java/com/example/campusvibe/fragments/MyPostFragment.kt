package com.example.campusvibe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.campusvibe.Models.Post
import com.example.campusvibe.adapter.MyPostRvAdapter
import com.example.campusvibe.databinding.FragmentMyPostBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class MyPostFragment : Fragment() {

    private lateinit var binding: FragmentMyPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)
        val postList = ArrayList<Post>()
        val adapter = MyPostRvAdapter(requireContext(), postList)
        binding.rv.layoutManager= StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rv.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id
            if (currentUserId != null) {
                val posts = SupabaseClient.client.postgrest["posts"].select {
                    filter {
                        eq("user_id", currentUserId)
                    }
                }.decodeList<Post>()
                postList.addAll(posts)
                adapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }
}