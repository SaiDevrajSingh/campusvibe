package com.example.campusvibe.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.Post.StoryActivity
import com.example.campusvibe.Models.Story
import com.example.campusvibe.databinding.StoryItemBinding
import com.bumptech.glide.Glide

class StoryAdapter(
    private val context: Context,
    private val storyList: ArrayList<Story>
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(StoryItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]
        Glide.with(context).load(story.imageUrl).into(holder.binding.storyImage)
        holder.binding.storyImage.setOnClickListener {
            val intent = Intent(context, StoryActivity::class.java)
            intent.putExtra("story_image_url", story.imageUrl)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }
}
