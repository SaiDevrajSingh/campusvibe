package com.example.campusvibe.ui.create

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.campusvibe.model.Post
import com.example.campusvibe.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import android.os.Build
import android.graphics.ImageDecoder
import android.provider.MediaStore

class StorageRepository(private val context: Context) {

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun uploadPost(mediaUri: Uri, caption: String, mediaType: String) {
        val mediaUrl = if (mediaType == "video") {
            uploadVideoToStorage(mediaUri)
        } else {
            val imageBitmap = compressImage(mediaUri)
            uploadImageToStorage(imageBitmap)
        }
        createPostInFirestore(mediaUrl, caption)
    }

    private fun compressImage(imageUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }
    }

    private suspend fun uploadImageToStorage(imageBitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val data = outputStream.toByteArray()

        val storageRef = storage.reference.child("posts/${UUID.randomUUID()}.jpg")
        val uploadTask = storageRef.putBytes(data).await()
        return uploadTask.storage.downloadUrl.await().toString()
    }

    private suspend fun uploadVideoToStorage(videoUri: Uri): String {
        val storageRef = storage.reference.child("posts/${UUID.randomUUID()}.mp4")
        val uploadTask = storageRef.putFile(videoUri).await()
        return uploadTask.storage.downloadUrl.await().toString()
    }

    private suspend fun createPostInFirestore(mediaUrl: String, caption: String) {
        val userId = auth.currentUser?.uid!!
        val user = firestore.collection("users").document(userId).get().await().toObject(User::class.java)!!

        val post = Post(
            id = UUID.randomUUID().toString(),
            userId = userId,
            username = user.username,
            imageUrl = mediaUrl,
            caption = caption,
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("posts").document(post.id).set(post).await()
    }
}
