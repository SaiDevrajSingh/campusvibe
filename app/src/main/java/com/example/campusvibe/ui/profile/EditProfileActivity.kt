package com.example.campusvibe.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
import com.example.campusvibe.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var imageUri: Uri? = null

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            binding.profileImage.setImageURI(imageUri)
        } else {
            Log.e("EditProfileActivity", "Image cropping failed", result.error)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener {
            finish()
        }

        binding.saveProfileButton.setOnClickListener {
            Log.d("EditProfileActivity", "Save button clicked")
            uploadProfileImage()
        }

        binding.changePhotoButton.setOnClickListener {
            cropImage.launch(
                options {
                    setAspectRatio(1, 1)
                }
            )
        }
    }

    private fun uploadProfileImage() {
        Log.d("EditProfileActivity", "Uploading profile image")
        imageUri?.let { uri ->
            val storageRef = FirebaseStorage.getInstance().reference
            val user = FirebaseAuth.getInstance().currentUser
            val profileImageRef = storageRef.child("profile_images/${user?.uid}.jpg")

            profileImageRef.putFile(uri)
                .addOnSuccessListener {
                    Log.d("EditProfileActivity", "Image upload successful")
                    profileImageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        updateUserProfile(downloadUrl.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("EditProfileActivity", "Image upload failed", e)
                }
        } ?: updateUserProfile(null) //In case user does not select a new image
    }

    private fun updateUserProfile(profileImageUrl: String?) {
        Log.d("EditProfileActivity", "Updating user profile")
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let {
            val userProfile = hashMapOf<String, Any>(
                "name" to binding.fullnameEditText.text.toString(),
                "username" to binding.usernameEditText.text.toString(),
                "bio" to binding.bioEditText.text.toString()
            )
            if (profileImageUrl != null) {
                userProfile["profileImageUrl"] = profileImageUrl
            }

            db.collection("users").document(it.uid)
                .set(userProfile, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("EditProfileActivity", "User profile update successful")
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("EditProfileActivity", "User profile update failed", e)
                }
        }
    }
}
