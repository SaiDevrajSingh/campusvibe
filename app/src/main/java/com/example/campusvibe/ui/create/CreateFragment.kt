package com.example.campusvibe.ui.create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentCreateBinding
import java.io.File

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private var mediaUri: Uri? = null
    private var mediaType: String? = null
    private var currentTab: UploadType = UploadType.POST

    private lateinit var recentPhotosAdapter: RecentPhotosAdapter

    private val selectMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            mediaUri = it
            val type = requireContext().contentResolver.getType(it)
            if (type?.startsWith("image") == true) {
                mediaType = "image"
                displaySelectedImage(it)
            } else if (type?.startsWith("video") == true) {
                mediaType = "video"
                displaySelectedVideo(it)
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            mediaUri?.let {
                mediaType = "image"
                displaySelectedImage(it)
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            showToast("Camera permission is required to take photos")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
        setupBottomTabs()
        loadRecentPhotos()
    }

    private fun setupViews() {
        // Initially show empty state
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.selectedImageView.visibility = View.GONE
    }

    private fun setupClickListeners() {
        // Close button
        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Next button
        binding.nextButton.setOnClickListener {
            when (currentTab) {
                UploadType.POST -> navigateToPostCreation()
                UploadType.STORY -> navigateToStoryCreation()
                UploadType.REEL -> showToast("Reels coming soon!")
            }
        }

        // Camera FAB
        binding.cameraFab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        // Image preview container click
        binding.imagePreviewContainer.setOnClickListener {
            selectMediaLauncher.launch("image/*,video/*")
        }
    }

    private fun setupBottomTabs() {
        binding.bottomTabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.text) {
                    "POST" -> {
                        currentTab = UploadType.POST
                        updateNextButtonState()
                    }
                    "STORY" -> {
                        currentTab = UploadType.STORY
                        updateNextButtonState()
                    }
                    "REEL" -> {
                        currentTab = UploadType.REEL
                        updateNextButtonState()
                    }
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun loadRecentPhotos() {
        val recentPhotos = getRecentPhotosFromGallery()
        setupRecentPhotosGrid(recentPhotos)
    }

    private fun getRecentPhotosFromGallery(): List<Uri> {
        val photos = mutableListOf<Uri>()
        try {
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val cursor = requireContext().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (it.moveToNext() && photos.size < 8) {
                    val id = it.getLong(idColumn)
                    val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                    photos.add(uri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return photos
    }

    private fun setupRecentPhotosGrid(photos: List<Uri>) {
        recentPhotosAdapter = RecentPhotosAdapter(photos) { uri ->
            mediaUri = uri
            mediaType = "image"
            displaySelectedImage(uri)
        }

        binding.recentPhotosRecyclerView.layoutManager = GridLayoutManager(context, 4)
        binding.recentPhotosRecyclerView.adapter = recentPhotosAdapter
    }

    private fun displaySelectedImage(uri: Uri) {
        binding.emptyStateLayout.visibility = View.GONE
        binding.selectedImageView.visibility = View.VISIBLE

        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(binding.selectedImageView)

        updateNextButtonState()
    }

    private fun displaySelectedVideo(uri: Uri) {
        binding.emptyStateLayout.visibility = View.GONE
        binding.selectedImageView.visibility = View.VISIBLE

        // For video, show a placeholder with play icon
        binding.selectedImageView.setImageResource(R.drawable.ic_videocam)
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        binding.nextButton.isEnabled = mediaUri != null
        binding.nextButton.alpha = if (mediaUri != null) 1.0f else 0.5f
    }

    private fun navigateToPostCreation() {
        if (mediaUri != null) {
            // For now, just show a success message
            showToast("Post creation coming soon!")
            findNavController().popBackStack()
        }
    }

    private fun navigateToStoryCreation() {
        if (mediaUri != null) {
            // For story, we can use the existing AddStoryActivity
            val intent = android.content.Intent(requireContext(), com.example.campusvibe.ui.story.AddStoryActivity::class.java).apply {
                putExtra("mediaUri", mediaUri.toString())
                putExtra("mediaType", mediaType)
            }
            startActivity(intent)
        }
    }

    private fun openCamera() {
        val file = File(requireContext().filesDir, "pic.jpg")
        mediaUri = FileProvider.getUriForFile(requireContext(), "com.example.campusvibe.fileprovider", file)
        takePictureLauncher.launch(mediaUri)
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class UploadType {
    POST, STORY, REEL
}

