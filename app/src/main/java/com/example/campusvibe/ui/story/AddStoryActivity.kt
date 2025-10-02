package com.example.campusvibe.ui.story

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campusvibe.databinding.ActivityAddStoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var selectedImageUri: Uri? = null

    private companion object {
        private const val RC_SELECT_IMAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, RC_SELECT_IMAGE)
        }

        binding.uploadStoryButton.setOnClickListener {
            uploadStory()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
        }
    }

    private fun uploadStory() {
        selectedImageUri?.let { uri ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val storageRef = FirebaseStorage.getInstance().reference.child("stories/$userId/${System.currentTimeMillis()}")
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val story = hashMapOf(
                        "userId" to userId,
                        "imageUrl" to downloadUrl.toString(),
                        "timestamp" to System.currentTimeMillis()
                    )
                    FirebaseFirestore.getInstance().collection("stories").add(story)
                    finish()
                }
            }
        }
    }
}
