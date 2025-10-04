package com.example.campusvibe.ui.addpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.databinding.ItemGalleryImageBinding

class GalleryAdapter(
    private val imageUris: List<String>,
    private val onImageClick: (String) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(imageUris[position])
    }

    override fun getItemCount() = imageUris.size

    inner class GalleryViewHolder(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onImageClick(imageUris[bindingAdapterPosition])
            }
        }

        fun bind(uri: String) {
            Glide.with(itemView.context)
                .load(uri)
                .into(binding.imageViewGallery)
        }
    }
}
