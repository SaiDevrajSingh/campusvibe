package com.example.campusvibe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
// StoryViewerActivity will be implemented later
import com.example.campusvibe.Models.Story
import com.example.campusvibe.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.jvm.java

class StoryAdapter(
    private val context: Context,
    private val storyList: List<Story>
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    var onStoryClick: ((Story) -> Unit)? = null

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
            .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                override fun onLoadFailed(e: com.bumptech.glide.load.engine.GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.e("StoryAdapter", "Failed to load story image: ${e?.message}")
                    return false
                }
                override fun onResourceReady(resource: android.graphics.drawable.Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                    Log.d("StoryAdapter", "Successfully loaded story image")
                    return false
                }
            })
            .into(holder.storyImage)
        
        // Show/hide add button for the first item (current user)
        holder.addStoryButton.visibility = if (position == 0) View.VISIBLE else View.GONE
        
        // Handle click on the story
        holder.itemView.setOnClickListener {
            if (position == 0) {
                // Open camera or gallery to add a new story
                onStoryClick?.invoke(story)
            } else {
                // TODO: Open story viewer
                // For now, just show a toast
                android.widget.Toast.makeText(context, "Opening story...", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        
        // Add click listener for the add button
        holder.addStoryButton.setOnClickListener {
            onStoryClick?.invoke(story)
        }
        
        // Set border color (temporarily all unviewed)
        holder.storyBorder.borderColor = context.resources.getColor(R.color.teal_200, null)
    }

    override fun getItemCount(): Int = storyList.size
}
