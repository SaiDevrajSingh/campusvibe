package com.example.campusvibe.ui.story

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.campusvibe.databinding.ActivityAddStoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var selectedImageUri: Uri? = null
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
            }
        }

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            selectImageLauncher.launch(intent)
        }

        binding.uploadStoryButton.setOnClickListener {
            uploadStory()
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
