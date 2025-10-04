package com.example.campusvibe.ui.reels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.campusvibe.databinding.FragmentReelsBinding
import com.example.campusvibe.ui.reels.ReelAdapter
import com.example.campusvibe.ui.reels.ReelsViewModel
import com.example.campusvibe.ui.reels.ReelViewHolder

class ReelsFragment : Fragment() {

    private var _binding: FragmentReelsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReelsViewModel by viewModels()
    private lateinit var adapter: ReelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        observeReels()
        viewModel.fetchReels()
    }

    private fun setupViewPager() {
        adapter = ReelAdapter()
        binding.reelsViewPager.adapter = adapter
        binding.reelsViewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        binding.reelsViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val recyclerView = binding.reelsViewPager.getChildAt(0) as RecyclerView
                (recyclerView.findViewHolderForAdapterPosition(position) as? ReelViewHolder)?.playVideo()
                (recyclerView.findViewHolderForAdapterPosition(position-1) as? ReelViewHolder)?.pauseVideo()
                (recyclerView.findViewHolderForAdapterPosition(position+1) as? ReelViewHolder)?.pauseVideo()
            }
        })
    }

    private fun observeReels() {
        viewModel.reels.observe(viewLifecycleOwner) { reels ->
            adapter.submitList(reels)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val recyclerView = binding.reelsViewPager.getChildAt(0) as RecyclerView
        for (i in 0 until adapter.itemCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? ReelViewHolder
            holder?.releasePlayer()
        }
        _binding = null
    }
}