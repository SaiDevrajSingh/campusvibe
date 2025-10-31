package com.example.campusvibe.Reel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.campusvibe.HomeActivity
import com.example.campusvibe.Models.Reel
import com.example.campusvibe.R
import com.example.campusvibe.databinding.ActivityUploadReelBinding
import com.example.campusvibe.utils.SupabaseClient
import com.example.campusvibe.utils.uploadVideo
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class UploadReelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadReelBinding
    private var videoUri: Uri? = null
    private var isUploading = false
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openVideoPicker()
        } else {
            showPermissionDeniedDialog()
        }
    }
    
    private val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) {
            Log.e("UploadReel", "No video selected")
            return@registerForActivityResult
        }
        
        handleVideoSelection(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadReelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        checkPermissions()
    }
    
    private fun setupUI() {
        binding.apply {
            // Initialize upload button as disabled
            uploadButton.isEnabled = false
            uploadButton.alpha = 0.5f
            
            selectVideoButton.setOnClickListener {
                if (checkPermissions()) {
                    openVideoPicker()
                }
            }

            uploadButton.setOnClickListener {
                if (!isUploading) {
                    uploadReel()
                }
            }

            cancelButton.setOnClickListener {
                if (!isUploading) {
                    finish()
                } else {
                    showCancelConfirmation()
                }
            }
            
            // Initialize progress bar
            uploadProgressBar.max = 100
            uploadProgressBar.progress = 0
        }
    }
    
    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                false
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                false
            }
        }
    }
    
    private fun openVideoPicker() {
        try {
            videoPickerLauncher.launch("video/*")
        } catch (e: Exception) {
            Log.e("UploadReel", "Error opening video picker", e)
            showError("Failed to open video picker: ${e.message}")
        }
    }
    
    private fun handleVideoSelection(uri: Uri) {
        videoUri = uri
        binding.apply {
            videoPreview.visibility = View.VISIBLE
            selectVideoButton.text = getString(R.string.select_video)
            
            try {
                // Get video file info
                val fileName = getFileName(uri)
                val fileSize = getFileSize(uri) / (1024 * 1024) // in MB
                
                // Check file size (max 50MB)
                if (fileSize > 50) {
                    showError(getString(R.string.video_too_large, 50))
                    clearSelection()
                    return@apply
                }
                
                // Get video duration using MediaMetadataRetriever
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(this@UploadReelActivity, uri)
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
                retriever.release()
                
                val durationInSeconds = duration / 1000
                
                // Check video duration (max 60 seconds)
                if (durationInSeconds > 60) {
                    showError(getString(R.string.video_too_long, 60))
                    clearSelection()
                    return@apply
                }
                
                // Update UI with video info
                videoInfoText.text = getString(R.string.selected_video, fileName)
                videoInfoText.visibility = View.VISIBLE
                videoDurationText.text = getString(R.string.duration, durationInSeconds)
                videoDurationText.visibility = View.VISIBLE
                
                // Enable upload button since we have a valid video
                uploadButton.isEnabled = true
                uploadButton.alpha = 1.0f
                
                // Set up video preview
                videoPreview.setVideoURI(uri)
                videoPreview.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    videoPreview.start()
                }
                
                videoPreview.setOnErrorListener { _, what, extra ->
                    Log.e("VideoPreview", "Error playing video: $what, $extra")
                    showError(getString(R.string.error_playing_video))
                    clearSelection()
                    false
                }
            } catch (e: Exception) {
                Log.e("VideoSelection", "Error processing video", e)
                showError("Error processing video: ${e.message}")
                clearSelection()
            }
        }
    }
    
    private fun clearSelection() {
        videoUri = null
        binding.apply {
            videoPreview.stopPlayback()
            videoPreview.visibility = View.GONE
            selectVideoButton.text = getString(R.string.select_video)
            videoInfoText.visibility = View.GONE
            videoDurationText.visibility = View.GONE
            
            // Disable upload button when no video is selected
            uploadButton.isEnabled = false
            uploadButton.alpha = 0.5f
        }
    }
    
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            result = cursor.getString(nameIndex)
        }
        return result ?: uri.path?.substringAfterLast('/') ?: "video.mp4"
    }

    private fun getFileSize(uri: Uri): Long {
        return contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            pfd.statSize
        } ?: 0L
    }
    
    private fun uploadReel() {
        val caption = binding.captionEditText.text.toString().trim()
        val currentUser = SupabaseClient.client.auth.currentUserOrNull()

        if (videoUri == null) {
            showError(getString(R.string.select_video_instruction))
            return
        }

        if (caption.isEmpty()) {
            binding.captionEditText.error = getString(R.string.add_a_caption)
            return
        }

        if (currentUser == null) {
            showError(getString(R.string.sign_in_required))
            return
        }
        
        isUploading = true
        updateUIForUploading(true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val videoUrl = uploadVideo(
                    context = this@UploadReelActivity,
                    uri = videoUri!!,
                    bucketName = "reels"
                ) { progress ->
                    runOnUiThread {
                        binding.uploadProgressBar.progress = progress.toInt()
                    }
                }
                
                if (videoUrl != null) {
                    // Save reel to database
                    val reel = Reel(
                        userId = currentUser.id,
                        videoUrl = videoUrl,
                        caption = caption
                    )
                    
                    SupabaseClient.client.postgrest
                        .from(Reel.TABLE)
                        .insert(reel)
                        
                    withContext(Dispatchers.Main) {
                        showSuccess(getString(R.string.upload_success))
                        navigateToHome()
                    }
                } else {
                    throw Exception("Failed to upload video")
                }
            } catch (e: Exception) {
                Log.e("UploadReel", "Error uploading reel", e)
                withContext(Dispatchers.Main) {
                    showError("${getString(R.string.upload_failed)}: ${e.message ?: getString(R.string.unknown_error)}")
                    updateUIForUploading(false)
                }
            } finally {
                isUploading = false
            }
        }
    }
    
    private fun updateUIForUploading(uploading: Boolean) {
        runOnUiThread {
            binding.apply {
                uploadButton.isEnabled = !uploading
                uploadButton.text = if (uploading) getString(R.string.uploading) else getString(R.string.upload)
                selectVideoButton.isEnabled = !uploading
                captionEditText.isEnabled = !uploading
                cancelButton.text = if (uploading) getString(R.string.cancel) else getString(android.R.string.cancel)
                
                if (uploading) {
                    uploadProgressBar.visibility = View.VISIBLE
                    uploadProgressBar.progress = 0
                } else {
                    uploadProgressBar.visibility = View.GONE
                }
            }
        }
    }
    
    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showSuccess(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
    
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.storage_permission_required)
            .setPositiveButton(R.string.grant_permission) { _, _ ->
                // Open app settings
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    private fun showCancelConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.cancel)
            .setMessage(R.string.confirm_cancel_upload)
            .setPositiveButton(R.string.yes) { _, _ ->
                isUploading = false
                updateUIForUploading(false)
                showError(getString(R.string.upload_cancelled))
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    override fun onPause() {
        super.onPause()
        binding.videoPreview.pause()
    }
    
    override fun onResume() {
        super.onResume()
        if (videoUri != null) {
            binding.videoPreview.start()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        binding.videoPreview.suspend()
    }
}
