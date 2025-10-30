package com.example.campusvibe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.R
import com.example.campusvibe.adapter.MyReelAdapter
import com.example.campusvibe.databinding.FragmentMyReelsBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class MyReelsFragment : Fragment() {

    private lateinit var binding: FragmentMyReelsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMyReelsBinding.inflate(inflater, container, false)
        var reelList=ArrayList<Reel>()
        var adapter= MyReelAdapter(requireContext(),reelList)
        binding.rv.layoutManager= StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.rv.adapter=adapter
        
        viewLifecycleOwner.lifecycleScope.launch {
            fetchMyReels(reelList, adapter)
        }

        return binding.root
    }

    private suspend fun fetchMyReels(reelList: ArrayList<Reel>, adapter: MyReelAdapter) {
        try {
            val supabase = SupabaseClient.client
            val currentUserId = supabase.auth.currentUserOrNull()?.id
            if (currentUserId != null) {
                val response = supabase.postgrest["reels"].select {
                    filter("uid", io.github.jan.supabase.postgrest.query.FilterOperator.EQ, currentUserId)
                }
                val reels = response.decodeList<Reel>()
                reelList.clear()
                reelList.addAll(reels)
                adapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    companion object {
    }
}