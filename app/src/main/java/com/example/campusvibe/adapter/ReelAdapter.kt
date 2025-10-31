package com.example.campusvibe.adapter

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ReelDgBinding

class ReelAdapter(var context: Context, var reelList: ArrayList<Reel>) : RecyclerView.Adapter<ReelAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: ReelDgBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReelDgBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = reelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reel = reelList[position]
        
        holder.binding.apply {
            // Set user profile image (you can update this to load from URL)
            profileImage.setImageResource(R.drawable.user)
            
            // Set caption
            caption.text = reel.caption
            
            // Show loading
            progressBar.visibility = View.VISIBLE
            
            try {
                // Set up media controller
                val mediaController = MediaController(context)
                mediaController.setAnchorView(videoView)
                videoView.setMediaController(mediaController)
                
                // Set video URI
                videoView.setVideoPath(reel.videoUrl)
                
                videoView.setOnPreparedListener { mp ->
                    progressBar.visibility = View.GONE
                    mp.start()
                    
                    // Adjust video scaling
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                }
                
                videoView.setOnErrorListener { _, what, extra ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "Error playing video", Toast.LENGTH_SHORT).show()
                    false
                }
                
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}