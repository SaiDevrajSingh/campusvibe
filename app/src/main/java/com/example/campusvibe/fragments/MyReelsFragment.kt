package com.example.campusvibe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.adapter.MyReelAdapter
import com.example.campusvibe.databinding.FragmentMyReelsBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class MyReelsFragment : Fragment() {

    private lateinit var binding: FragmentMyReelsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyReelsBinding.inflate(inflater, container, false)
        val reelList = ArrayList<Reel>()
        val adapter = MyReelAdapter(requireContext(), reelList)
        binding.rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rv.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id
            if (currentUserId != null) {
                val reels = SupabaseClient.client.postgrest["reels"].select {
                    filter {
                        eq("user_id", currentUserId)
                    }
                }.decodeList<Reel>()
                reelList.addAll(reels)
                adapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }
}