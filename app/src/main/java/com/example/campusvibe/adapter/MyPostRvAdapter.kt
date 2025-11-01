package com.example.campusvibe.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusvibe.Models.Post
import com.example.campusvibe.Models.User
import com.example.campusvibe.PostDetailActivity
import com.example.campusvibe.databinding.MyPostRvDesignBinding
import com.example.campusvibe.utils.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class MyPostRvAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<MyPostRvAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: MyPostRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyPostRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        Glide.with(context).load(post.imageUrl).into(holder.binding.postImage)

        holder.binding.postImage.setOnClickListener {
            (context as AppCompatActivity).lifecycleScope.launch {
                val intent = Intent(context, PostDetailActivity::class.java)
                intent.putExtra("postUrl", post.imageUrl)
                intent.putExtra("caption", post.caption)

                val user = client.from("users").select {
                    filter {
                        eq("id", post.userId)
                    }
                }.decodeSingleOrNull<User>()

                intent.putExtra("profileImageUrl", user?.image)
                intent.putExtra("username", user?.name)
                context.startActivity(intent)
            }
        }
    }
}
