package com.example.campusvibe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ReelDgBinding


class ReelAdapter (var context: Context, var reelList:ArrayList<Reel>): RecyclerView.Adapter<ReelAdapter.ViewHolder>(){
    inner class ViewHolder(var binding: ReelDgBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = ReelDgBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.profileImage.setImageResource(R.drawable.user)
        holder.binding.caption.setText(reelList.get(position).caption)
        holder.binding.videoView.setVideoPath(reelList.get(position).reelUrl)
        holder.binding.videoView.setOnPreparedListener{
            holder.binding.progressBar.visibility= View.GONE
            holder.binding.videoView.start()
        }


    }
}