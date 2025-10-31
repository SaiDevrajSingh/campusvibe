package com.example.campusvibe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.R
import com.example.campusvibe.adapter.ReelAdapter
import com.example.campusvibe.databinding.FragmentReelBinding
import com.example.campusvibe.utils.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class ReelFragment : Fragment() {
    private lateinit var binding: FragmentReelBinding
    private lateinit var adapter: ReelAdapter
    private var reelList = ArrayList<Reel>()
    private val TAG = "ReelFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReelBinding.inflate(inflater, container, false)
        adapter = ReelAdapter(requireContext(), reelList)
        binding.viewPager.adapter = adapter
        
        // Show loading indicator
        showLoading(true)
        
        viewLifecycleOwner.lifecycleScope.launch {
            fetchReels()
        }
        return binding.root
    }

    private suspend fun fetchReels() {
        try {
            Log.d(TAG, "Fetching reels from database...")
            val response = SupabaseClient.client.postgrest["reels"].select()
            val fetchedReels = response.decodeList<Reel>()
            
            Log.d(TAG, "Fetched ${fetchedReels.size} reels")
            
            reelList.clear()
            reelList.addAll(fetchedReels)
            reelList.reverse()
            adapter.notifyDataSetChanged()
            
            // Hide loading indicator
            showLoading(false)
            
            if (reelList.isEmpty()) {
                Toast.makeText(requireContext(), "No reels available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching reels: ${e.message}", e)
            showLoading(false)
            Toast.makeText(requireContext(), "Error loading reels: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Show or hide loading indicator
     */
    private fun showLoading(show: Boolean) {
        // If your layout has a progress bar, show/hide it here
        // For now, we'll just log the state
        Log.d(TAG, "Loading: $show")
    }

    companion object {
        fun newInstance() = ReelFragment()
    }
}