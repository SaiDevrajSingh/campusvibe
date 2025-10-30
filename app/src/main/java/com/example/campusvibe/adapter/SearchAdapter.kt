package com.example.campusvibe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.Models.User
import com.example.campusvibe.R
import com.example.campusvibe.databinding.SearchRvBinding
import com.bumptech.glide.Glide

class SearchAdapter(var context: Context, var userList: ArrayList<User>):RecyclerView.Adapter<SearchAdapter.viewHolder>() {
    inner class viewHolder(var binding: SearchRvBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var binding =SearchRvBinding.inflate(LayoutInflater.from(context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        Glide.with(context).load(userList.get(position).image).placeholder(R.drawable.user).into(holder.binding.profileImage)
        holder.binding.name.text=userList.get(position).name
        holder.binding.follow.text = "Follow"
    }
}