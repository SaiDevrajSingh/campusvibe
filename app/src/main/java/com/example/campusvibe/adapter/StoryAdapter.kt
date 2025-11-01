package com.example.campusvibe.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.campusvibe.Models.Story
import com.example.campusvibe.R
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter(
    private val context: Context,
    private val storyList: List<Story>
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storyImage: CircleImageView = view.findViewById(R.id.storyImage)
        val storyBorder: CircleImageView = view.findViewById(R.id.storyBorder)
        val username: TextView = view.findViewById(R.id.username)
        val addStoryButton: ImageView = view.findViewById(R.id.addStoryButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]
        
        Log.d("StoryAdapter", "Binding story at position $position: ${story.id}")
        Log.d("StoryAdapter", "Story URL: ${story.imageUrl}")
        Log.d("StoryAdapter", "Username: ${story.username}")
        
        // Load story image with Glide
        Glide.with(context)
            .load(story.imageUrl)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.storyImage)
        
        // Remove the add story button as requested
        holder.addStoryButton.visibility = View.GONE
        
        // Handle click on the story to view it
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("imageUrl", story.imageUrl)
                putString("username", story.username)
                putString("profileImageUrl", story.profileImage)
            }
            try {
                holder.itemView.findNavController().navigate(R.id.story_view, bundle)
            } catch (e: IllegalStateException) {
                Log.e("StoryAdapter", "Navigation failed: ${e.message}")
            }
        }
        
        // Set border color (temporarily all unviewed)
        holder.storyBorder.borderColor = context.resources.getColor(R.color.teal_200, null)
    }

    override fun getItemCount(): Int = storyList.size
}
