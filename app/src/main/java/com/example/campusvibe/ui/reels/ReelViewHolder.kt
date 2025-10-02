package com.example.campusvibe.ui.reels

import androidx.recyclerview.widget.RecyclerView
import com.example.campusvibe.databinding.ItemReelBinding
import com.example.campusvibe.model.Post
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView

class ReelViewHolder(private val binding: ItemReelBinding) : RecyclerView.ViewHolder(binding.root) {

    private var exoPlayer: ExoPlayer? = null

    fun bind(post: Any) {
        val post = post as Post
        binding.usernameTextView.text = post.username
        binding.captionTextView.text = post.caption

        initializePlayer()
        exoPlayer?.let {
            val mediaItem = MediaItem.fromUri(post.imageUrl)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.playWhenReady = true
            it.repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(binding.root.context).build()
        binding.playerView.player = exoPlayer
    }

    fun playVideo() {
        exoPlayer?.play()
    }

    fun pauseVideo() {
        exoPlayer?.pause()
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
}

