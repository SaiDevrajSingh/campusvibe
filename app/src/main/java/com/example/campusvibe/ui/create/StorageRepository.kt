package com.example.campusvibe.ui.create

import android.net.Uri
import com.example.campusvibe.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class StorageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun uploadMediaAndCreatePost(
        mediaUri: Uri,
        caption: String,
        mediaType: String
    ) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val username = user.displayName ?: ""
            val userAvatarUrl = user.photoUrl.toString()

            // 1. Upload media to Firebase Storage
            val storageRef = storage.reference
            val mediaRef = storageRef.child("posts/${UUID.randomUUID()}")
            val uploadTask = mediaRef.putFile(mediaUri).await()
            val mediaUrl = uploadTask.storage.downloadUrl.await().toString()

            // 2. Create post object
            val postId = db.collection("posts").document().id
            val post = Post(
                id = postId,
                userId = userId,
                username = username,
                userProfileImageUrl = userAvatarUrl,
                imageUrl = mediaUrl,
                caption = caption,
                timestamp = Date(),
                likes = 0,
                likedBy = emptyList()
            )

            // 3. Save post to Firestore
            db.collection("posts").document(postId).set(post).await()
        }
    }
}
