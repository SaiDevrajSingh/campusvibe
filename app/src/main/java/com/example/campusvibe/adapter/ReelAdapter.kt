package com.example.campusvibe.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.Models.User
import com.example.campusvibe.ReelDetailActivity
import com.example.campusvibe.databinding.ReelDgBinding
import com.example.campusvibe.utils.SupabaseClient.client
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ReelAdapter(var context: Context, var reelList: ArrayList<Reel>) :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ReelDgBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReelDgBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reel = reelList[position]
        holder.binding.videoView.setVideoPath(reel.videoUrl)
        holder.binding.videoView.setOnPreparedListener {
            holder.binding.progressBar.visibility = GONE
            it.start()
        }

        holder.binding.videoView.setOnClickListener {
            (context as androidx.appcompat.app.AppCompatActivity).lifecycleScope.launch {
                val intent = Intent(context, ReelDetailActivity::class.java)
                intent.putExtra("videoUrl", reel.videoUrl)
                intent.putExtra("caption", reel.caption)

                val user = client.from("users").select {
                    filter {
                        eq("id", reel.userId)
                    }
                }.data.firstOrNull()?.let {
                    Json.decodeFromString<User>(it.toString())
                }

                intent.putExtra("profileImageUrl", user?.image)
                intent.putExtra("username", user?.name)
                context.startActivity(intent)
            }
        }
    }
}