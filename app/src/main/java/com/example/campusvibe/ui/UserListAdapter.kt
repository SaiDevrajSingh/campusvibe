package com.example.campusvibe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.model.User

class UserListAdapter(private val onClick: (User) -> Unit) : ListAdapter<User, UserListAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(old: User, new: User) = old.uid == new.uid
            override fun areContentsTheSame(old: User, new: User) = old == new
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
        val tvName: TextView = view.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_user_row, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = getItem(position)
        holder.tvName.text = user.displayName
        Glide.with(holder.itemView.context)
            .load(user.photoUrl)
            .placeholder(R.drawable.ic_profile)
            .into(holder.ivAvatar)

        holder.itemView.setOnClickListener { onClick(user) }
    }
}
