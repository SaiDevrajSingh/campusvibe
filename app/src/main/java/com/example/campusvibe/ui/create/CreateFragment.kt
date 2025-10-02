package com.example.campusvibe.ui.create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.campusvibe.R
import com.example.campusvibe.databinding.FragmentCreateBinding
import java.io.File

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateViewModel by viewModels {
        CreateViewModelFactory(StorageRepository(requireContext().applicationContext))
    }

    private var mediaUri: Uri? = null
    private var mediaType: String? = null

    private val selectMediaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            mediaUri = it
            val type = requireContext().contentResolver.getType(it)
            if (type?.startsWith("image") == true) {
                mediaType = "image"
                binding.postImageView.setImageURI(it)
            } else if (type?.startsWith("video") == true) {
                mediaType = "video"
                binding.postImageView.setImageResource(R.drawable.ic_videocam)
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            mediaUri?.let {
                mediaType = "image"
                binding.postImageView.setImageURI(it)
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
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

        binding.selectImageButton.setOnClickListener {
            selectMediaLauncher.launch("image/*,video/*")
        }

        binding.openCameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.uploadPostButton.setOnClickListener {
            val caption = binding.captionEditText.text.toString()
            if (mediaUri != null && mediaType != null && caption.isNotBlank()) {
                viewModel.uploadPost(mediaUri!!, caption, mediaType!!)
            } else {
                Toast.makeText(requireContext(), "Please select a file and write a caption", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is UploadStatus.Loading -> {
                    // Show a progress bar
                    binding.uploadPostButton.isEnabled = false
                    binding.uploadPostButton.text = "Uploading..."
                }
                is UploadStatus.Success -> {
                    Toast.makeText(requireContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack() // Go back to the previous screen
                }
                is UploadStatus.Error -> {
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                    binding.uploadPostButton.isEnabled = true
                    binding.uploadPostButton.text = "Upload Post"
                }
            }
        }
    }

    private fun openCamera() {
        val file = File(requireContext().filesDir, "pic.jpg")
        mediaUri = FileProvider.getUriForFile(requireContext(), "com.example.campusvibe.fileprovider", file)
        takePictureLauncher.launch(mediaUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

