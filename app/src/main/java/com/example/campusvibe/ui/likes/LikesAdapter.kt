package com.example.campusvibe.ui.likes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.campusvibe.R
import com.example.campusvibe.data.PostWithUser
import com.example.campusvibe.databinding.ItemLikedPostBinding

class LikesAdapter(
    var posts: List<PostWithUser>
) : RecyclerView.Adapter<LikesAdapter.LikedPostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedPostViewHolder {
        val binding = ItemLikedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikedPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LikedPostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class LikedPostViewHolder(private val binding: ItemLikedPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(postWithUser: PostWithUser) {
            binding.usernameTextView.text = postWithUser.user.username
            binding.captionTextView.text = postWithUser.post.caption

            Glide.with(itemView.context)
                .load(postWithUser.post.imageUrl)
                .into(binding.postImageView)

            if (postWithUser.user.profilePictureUrl != null) {
                Glide.with(itemView.context)
                    .load(postWithUser.user.profilePictureUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.userProfileImageView)
            } else {
                binding.userProfileImageView.setImageResource(R.drawable.ic_profile)
            }
        }
    }
}

