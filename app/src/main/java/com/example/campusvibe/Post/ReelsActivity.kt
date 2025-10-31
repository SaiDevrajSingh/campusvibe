package com.example.campusvibe.Post

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.HomeActivity
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.databinding.ActivityReelsBinding
import com.example.campusvibe.utils.SupabaseClient
import com.example.campusvibe.utils.uploadVideo
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class ReelsActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }
    private lateinit var videoUrl: String
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            lifecycleScope.launch {
                binding.progressBar.visibility = View.VISIBLE
                videoUrl = uploadVideo(this@ReelsActivity, it, "reel_videos")!!
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.progressBar.visibility = View.GONE

        binding.selectReel.setOnClickListener {
            launcher.launch("video/*")
        }
        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
            finish()
        }
        binding.postButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val supabase = SupabaseClient.client
                    val currentUser = supabase.auth.currentUserOrNull()

                    currentUser?.let {
                        val reel = Reel(
                            reelUrl = videoUrl,
                            caption = binding.caption.editText?.text.toString()
                        )

                        supabase.postgrest["reels"].insert(reel)

                        startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("ReelsActivity", "Error creating reel", e)
                }
            }
        }
    }
}