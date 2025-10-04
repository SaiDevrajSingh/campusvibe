package com.example.campusvibe.ui.create

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R

class RecentPhotosAdapter(
    private val photos: List<Uri>,
    private val onPhotoClick: (Uri) -> Unit
) : RecyclerView.Adapter<RecentPhotosAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo_grid, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount() = photos.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoImage = itemView.findViewById<android.widget.ImageView>(R.id.photo_image)
        private val cameraIcon = itemView.findViewById<android.widget.ImageView>(R.id.camera_icon)

        fun bind(uri: Uri) {
            Glide.with(itemView.context)
                .load(uri)
                .centerCrop()
                .into(photoImage)

            // Show camera icon for the first item (camera placeholder)
            cameraIcon.visibility = if (adapterPosition == 0) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onPhotoClick(uri)
            }
        }
    }
}
