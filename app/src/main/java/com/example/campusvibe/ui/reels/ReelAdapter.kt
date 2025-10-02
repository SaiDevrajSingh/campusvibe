package com.example.campusvibe.ui.reels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.databinding.ItemReelBinding
import com.example.campusvibe.model.Post

class ReelAdapter() : RecyclerView.Adapter<ReelViewHolder>() {

    private val reels: MutableList<Post> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ReelViewHolder {
        val binding = ItemReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holder.bind(reels[position])
    }

    override fun getItemCount() = reels.size

    fun submitList(newReels: List<Post>) {
        reels.clear()
        reels.addAll(newReels)
        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        for (i in 0 until recyclerView.childCount) {
            val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) as? ReelViewHolder
            holder?.releasePlayer()
        }
    }
}

