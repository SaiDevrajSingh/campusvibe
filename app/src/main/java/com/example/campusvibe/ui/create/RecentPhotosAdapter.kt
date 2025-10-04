package com.example.campusvibe.ui.create

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ItemPhotoGridBinding

class RecentPhotosAdapter(
    private val photos: List<Uri>,
    private val onPhotoClick: (Uri) -> Unit
) : RecyclerView.Adapter<RecentPhotosAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount() = photos.size

    inner class PhotoViewHolder(private val binding: ItemPhotoGridBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            Glide.with(itemView.context)
                .load(uri)
                .centerCrop()
                .into(binding.photoImage)

            // Show camera icon for the first item (camera placeholder)
            binding.cameraIcon.visibility = if (bindingAdapterPosition == 0) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onPhotoClick(uri)
            }
        }
    }
}
