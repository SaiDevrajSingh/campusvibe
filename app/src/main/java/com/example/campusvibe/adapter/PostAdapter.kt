package com.example.campusvibe.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.Models.Post
import com.example.campusvibe.R
import com.example.campusvibe.databinding.PostRvBinding
import com.bumptech.glide.Glide

class PostAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.MyHolder>() {
    inner class MyHolder(var binding: PostRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        var binding = PostRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size

    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.binding.profileImage.setImageResource(R.drawable.user)
        holder.binding.name.text = ""

        Glide.with(context).load(postList.get(position).postUrl).placeholder(R.drawable.loading)
            .into(holder.binding.postImage)
        
        holder.binding.time.text = ""
        holder.binding.share.setOnClickListener{
            var i = Intent(Intent.ACTION_SEND)
            i.type="text/plain"
            i.putExtra(Intent.EXTRA_TEXT,postList.get(position).postUrl)
            context.startActivity(i)
        }
        holder.binding.caption.text = postList.get(position).caption
        holder.binding.like.setOnClickListener {
            holder.binding.like.setImageResource(R.drawable.heart_like)

        }
    }

}