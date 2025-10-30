package com.example.campusvibe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.databinding.StoryPageBinding
import com.bumptech.glide.Glide

class StoryPagerAdapter(
    private val context: Context,
    private val storyImageUrls: ArrayList<String>
) : RecyclerView.Adapter<StoryPagerAdapter.StoryPageViewHolder>() {

    inner class StoryPageViewHolder(val binding: StoryPageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPageViewHolder {
        return StoryPageViewHolder(StoryPageBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: StoryPageViewHolder, position: Int) {
        Glide.with(context).load(storyImageUrls[position]).into(holder.binding.storyImage)
    }

    override fun getItemCount(): Int {
        return storyImageUrls.size
    }
}
