package com.example.campusvibe.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.R
import com.example.campusvibe.adapter.ReelAdapter
import com.example.campusvibe.databinding.FragmentReelBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class ReelFragment : Fragment() {
    private lateinit var  binding: FragmentReelBinding
    lateinit var  adapter: ReelAdapter
    var reelList= ArrayList<Reel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentReelBinding.inflate(inflater, container, false)
        adapter= ReelAdapter(requireContext(),reelList)
        binding.viewPager.adapter=adapter
        viewLifecycleOwner.lifecycleScope.launch {
            fetchReels()
        }
        return binding.root
    }

    private suspend fun fetchReels() {
        try {
            val response = SupabaseClient.client.postgrest["reels"].select()
            val fetchedReels = response.decodeList<Reel>()
            reelList.clear()
            reelList.addAll(fetchedReels)
            reelList.reverse()
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    companion object {

    }
}